<template>
  <router-view />
</template>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}
/* 自定义极简滚动条 */
::-webkit-scrollbar {
  width: 6px;
  /* 变窄 */
}

::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  /* 浅色半透明 */
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: rgba(37, 99, 235, 0.4);
  /* 鼠标移上去变蓝色 */
}
</style>