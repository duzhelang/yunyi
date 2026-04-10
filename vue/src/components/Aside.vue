<template>
	<el-menu :default-openeds="opens" style="min-height: 100%; overflow-x: hidden" background-color="#1e293b"
		text-color="#fff" active-text-color="#ffd04b" :collapse-transition="false" :collapse="isCollapse" router
		class="aside-menu" 		>
		<div style="height: 60px; line-height: 60px; text-align: center">
			<b style="color: white; margin-left: 5px" v-show="logoTextShow">云医智护--糖尿病诊断系统</b>
			<br>
			<b style="color: white; margin-left: 5px" v-show="logoTextShow">---糖尿病诊断系统</b>
		</div>
		<div v-for="item in menus" :key="item.id">
			<div v-if="item.path">
				<el-menu-item :index="item.path">
					<i :class="item.icon || 'el-icon-menu'"></i>
					<span slot="title">{{ item.name }}</span>
				</el-menu-item>
			</div>
			<div v-else-if="item.children && item.children.length">
				<el-submenu :index="item.id + ''">
					<template slot="title">
						<i :class="item.icon || 'el-icon-menu'"></i>
						<span slot="title">{{ item.name }}</span>
					</template>
					<div v-for="subItem in item.children" :key="subItem.id">
						<el-menu-item :index="subItem.path">
							<i :class="subItem.icon || 'el-icon-menu'"></i>
							<span slot="title">{{ subItem.name }}</span>
						</el-menu-item>
					</div>
				</el-submenu>
			</div>
		</div>
	</el-menu>
</template>

<script>
	export default {
		name: "Aside",
		props: {
			isCollapse: {
				type: Boolean,
				default: false
			},
			logoTextShow: {
				type: Boolean,
				default: true
			}
		},
		data() {
			return {
				menus: [],
				opens: []
			}
		},
		created() {
			try {
				const storedMenus = localStorage.getItem("menus");
				if (storedMenus) {
					this.menus = JSON.parse(storedMenus);
					this.opens = this.menus.map(v => v.id + '');
				}
			} catch (error) {
				console.error('解析菜单数据失败:', error);
				this.menus = [];
				this.opens = [];
			}
		}
	}
</script>

<!-- 1. 移除scoped,让样式作用到菜单本身;2. 给.el-menu加固定样式 -->
<style>
	/* 核心:固定侧边栏菜单(Aside.vue的根元素是el-menu) */
	.aside-menu {
		position: fixed !important;
		/* 固定定位 */
		top: 0;
		/* 顶部对齐 */
		left: 0;
		/* 左侧对齐 */
		height: 100vh !important;
		/* 占满视口高度 */
		width: 200px !important;
		/* 侧边栏宽度 */
		z-index: 999 !important;
		/* 确保在最上层,不被遮挡 */
		overflow-y: auto;
		/* 菜单过多时自身滚动 */
	}

	/* 解决收缩菜单文字不消失问题 */
	.el-menu--collapse span {
		visibility: hidden;
	}

	/* 菜单选中/hover样式 */
	.el-menu-item.is-active {
		background-color: #3d5a80 !important;
	}

	.el-menu-item:hover {
		background-color: #3d5a80 !important;
	}

	.el-submenu__title:hover {
		background-color: #3d5a80 !important;
	}
</style>

<!-- 可选:如果父组件有el-aside,需在布局文件(如Layout.vue)加以下样式 -->
<style>
	/* 若Layout.vue中有<el-aside>,添加此样式避免重复占位 */
	.el-aside {
		width: 200px !important;
		/* 和侧边栏宽度一致 */
		height: 100vh;
		padding: 0 !important;
		/* 清除默认内边距 */
	}

	/* 主内容区加左边距,避免被固定菜单遮挡 
	 .el-main {
		margin-left: 200px !important;
	} */
</style>