<template>
  <el-card class="knowledge-card">
    <template #header>
      <div class="card-header">
        <span>RAG 知识库管理（管理员）</span>
        <el-button type="danger" @click="handleClear">清空知识库</el-button>
      </div>
    </template>

    <div class="upload-area">
      <el-upload
        class="upload-demo"
        drag
        action="/backend/knowledge/upload"
        :headers="headers"
        :on-success="handleSuccess"
        :on-error="handleError"
        accept=".pdf"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽 PDF 文件到此处 或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持上传业务 PDF 手册，自动进行分片与向量化
          </div>
        </template>
      </el-upload>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '../api/request';

const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}));

const handleSuccess = () => {
  ElMessage.success('资料上传并处理成功');
};

const handleError = (err) => {
  ElMessage.error('上传失败: ' + err.message);
};

const handleClear = () => {
  ElMessageBox.confirm('确定要清空所有向量数据吗？此操作不可撤销', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete('/knowledge/clear');
      ElMessage.success('知识库已清空');
    } catch (err) {
      ElMessage.error('操作失败');
    }
  });
};
</script>

<style scoped>
.knowledge-card { margin: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.upload-area { padding: 40px 0; text-align: center; }
</style>
