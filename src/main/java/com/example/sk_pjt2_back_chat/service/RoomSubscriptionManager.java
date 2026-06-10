package com.example.sk_pjt2_back_chat.service;

import com.example.sk_pjt2_back_chat.subscriber.RedisChatSubscriber;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomSubscriptionManager {

    private final RedisMessageListenerContainer container;
    private final RedisChatSubscriber subscriber;

    // roomUUID -> 이 서버의 로컬 구독자 수
    private final Map<String, Integer> roomRefCount = new ConcurrentHashMap<>();
    // sessionId -> (subscriptionId -> roomUUID)
    private final Map<String, Map<String, String>> sessionSubs = new ConcurrentHashMap<>();

    public RoomSubscriptionManager(RedisMessageListenerContainer container, RedisChatSubscriber subscriber) {
        this.container = container;
        this.subscriber = subscriber;
    }

    public synchronized void addSubscription(String sessionId, String subscriptionId, String roomUUID) {
        sessionSubs.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>()).put(subscriptionId, roomUUID);
        int count = roomRefCount.merge(roomUUID, 1, Integer::sum);
        if (count == 1) {  // 이 서버에서 이 방의 첫 구독자 → Redis 채널 구독 시작
            container.addMessageListener(subscriber, new ChannelTopic("chat:room:" + roomUUID));
            System.out.println("[Redis] 채널 구독 시작: chat:room:" + roomUUID);
        }
    }

    public synchronized void removeSubscription(String sessionId, String subscriptionId) {
        Map<String, String> subs = sessionSubs.get(sessionId);
        if (subs == null) return;
        String roomUUID = subs.remove(subscriptionId);
        if (roomUUID != null) decrement(roomUUID);
        if (subs.isEmpty()) sessionSubs.remove(sessionId);
    }

    public synchronized void removeSession(String sessionId) {  // DISCONNECT 시 일괄 정리
        Map<String, String> subs = sessionSubs.remove(sessionId);
        if (subs == null) return;
        subs.values().forEach(this::decrement);
    }

    private void decrement(String roomUUID) {
        Integer count = roomRefCount.computeIfPresent(roomUUID, (k, v) -> v - 1);
        if (count != null && count <= 0) {  // 이 서버에서 마지막 구독자 → 채널 구독 해제
            roomRefCount.remove(roomUUID);
            container.removeMessageListener(subscriber, new ChannelTopic("chat:room:" + roomUUID));
            System.out.println("[Redis] 채널 구독 해제: chat:room:" + roomUUID);
        }
    }
}