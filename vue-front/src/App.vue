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

/* 隐藏滚动条但保留滚动功能 */
::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

/* Firefox */
* {
  scrollbar-width: none;
}

/* IE and Edge */
html {
  -ms-overflow-style: none;
}
</style>