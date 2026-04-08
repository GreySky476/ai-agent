<template>
  <div class="chat-container">
    <div class="message-list" ref="messageList">
      <div v-for="(msg, index) in messages" :key="index" :class="['message-item', msg.role]">
        <el-avatar :size="40" :icon="msg.role === 'user' ? 'User' : 'Robot'" />
        <div class="message-content">
          <div class="sender-name">{{ msg.role === 'user' ? '我' : 'AI 助手' }}</div>
          <div class="bubble shadow-sm">{{ msg.content }}</div>
        </div>
      </div>
    </div>
    <div class="input-area">
      <el-input
        v-model="userInput"
        type="textarea"
        :rows="3"
        placeholder="请输入您的问题，Shift + Enter 换行"
        @keyup.enter.exact.prevent="handleSend"
        resize="none"
      />
      <div class="btn-group">
        <el-button type="info" plain @click="messages.length = 0">重置对话</el-button>
        <el-button type="primary" :loading="loading" @click="handleSend">
          发送消息 <el-icon class="el-icon--right"><Promotion /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue';
import request from '../api/request';

const userInput = ref('');
const loading = ref(false);
const messages = reactive([]);
const messageList = ref(null);
const sessionId = ref(`session-${Math.floor(Math.random() * 10000)}`);

const scrollToBottom = async () => {
  await nextTick();
  if (messageList.value) {
    messageList.value.scrollTo({
      top: messageList.value.scrollHeight,
      behavior: 'smooth'
    });
  }
};

const handleSend = async () => {
  if (!userInput.value.trim() || loading.value) return;

  const content = userInput.value;
  messages.push({ role: 'user', content });
  userInput.value = '';
  loading.value = true;
  await scrollToBottom();

  try {
    // 关键修复：显式将消息内容包装在 JSON 对象中
    const res = await request.post(`/chat/${sessionId.value}`, {
      message: content
    });
    console.log('Chat response:', res); // 添加日志，确认后端返回的数据结构
    messages.push({ role: 'assistant', content: res.data });
    await scrollToBottom();
  } catch (err) {
    console.error('Chat error:', err); // 添加错误日志，确认失败的具体原因
    messages.push({ role: 'assistant', content: '抱歉，服务出现了点小问题，请稍后再试。' });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  messages.push({ role: 'assistant', content: '您好！我是您的智能客服助手。我已经准备好结合知识库为您服务了，请问有什么可以帮您？' });
});
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
  background-color: #f9f9f9;
}
.message-item {
  display: flex;
  margin-bottom: 25px;
  gap: 15px;
}
.message-item.user {
  flex-direction: row-reverse;
}
.message-content {
  max-width: 70%;
}
.user .message-content {
  text-align: right;
}
.sender-name {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}
.bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
  position: relative;
}
.assistant .bubble {
  background-color: #fff;
  color: #333;
  border: 1px solid #e4e7ed;
  border-top-left-radius: 0;
}
.user .bubble {
  background-color: #409eff;
  color: #fff;
  border-top-right-radius: 0;
  text-align: left;
}
.input-area {
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}
.btn-group {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  gap: 10px;
}
</style>
