package com.mokakbob.cache;

import com.mokakbob.domain.chat.cache.CachedChatMessage;
import com.mokakbob.domain.chat.domain.ChatMessage;
import java.util.List;

public interface ChatMessageStore {

    void cacheMessage(ChatMessage message);

    List<CachedChatMessage> loadMessages(Long roomId, String rawCursor, int sizePlusOne);
}
