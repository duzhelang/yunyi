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
					<el-icon v-if="item.icon">
						<component :is="getIconComponent(item.icon)" />
					</el-icon>
					<el-icon v-else><Menu /></el-icon>
					<template #title>{{ item.name }}</template>
				</el-menu-item>
			</div>
			<div v-else-if="item.children && item.children.length">
				<el-sub-menu :index="item.id + ''">
					<template #title>
						<el-icon v-if="item.icon">
							<component :is="getIconComponent(item.icon)" />
						</el-icon>
						<el-icon v-else><Menu /></el-icon>
						{{ item.name }}
					</template>
					<div v-for="subItem in item.children" :key="subItem.id">
						<el-menu-item :index="subItem.path">
							<el-icon v-if="subItem.icon">
								<component :is="getIconComponent(subItem.icon)" />
							</el-icon>
							<el-icon v-else><Menu /></el-icon>
							<template #title>{{ subItem.name }}</template>
						</el-menu-item>
					</div>
				</el-sub-menu>
			</div>
		</div>
	</el-menu>
</template>

<script>
import { Menu, Coffee, Document, House, User, Grid, Upload, Download, Delete, Edit, Plus, Search, Message, ArrowDown, Loading, Check, Close, Top, Bottom, Warning } from '@element-plus/icons-vue';

export default {
	name: "Aside",
	components: { Menu, Coffee, Document, House, User, Grid, Upload, Download, Delete, Edit, Plus, Search, Message, ArrowDown, Loading, Check, Close, Top, Bottom, Warning },
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
	methods: {
		// 图标映射函数，将旧的 el-icon-* 格式转换为新的组件名
		getIconComponent(iconName) {
			const iconMap = {
				'el-icon-coffee': 'Coffee',
				'el-icon-document': 'Document',
				'el-icon-house': 'House',
				'el-icon-user': 'User',
				'el-icon-s-grid': 'Grid',
				'el-icon-upload': 'Upload',
				'el-icon-upload2': 'Upload',
				'el-icon-download': 'Download',
				'el-icon-remove-outline': 'Delete',
				'el-icon-edit': 'Edit',
				'el-icon-plus': 'Plus',
				'el-icon-circle-plus-outline': 'Plus',
				'el-icon-search': 'Search',
				'el-icon-message': 'Message',
				'el-icon-arrow-down': 'ArrowDown',
				'el-icon-loading': 'Loading',
				'el-icon-question': 'Menu',
				'el-icon-time': 'Menu',
				'el-icon-circle-check': 'Check',
				'el-icon-close': 'Close',
				'el-icon-top': 'Top',
				'el-icon-bottom': 'Bottom',
				'el-icon-warning': 'Warning'
			};
			return iconMap[iconName] || 'Menu';
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

	.el-sub-menu__title:hover {
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

	/* 主内容区加左边距,避免被固定菜单遮挡 */
	/* .el-main {
		margin-left: 200px !important;
	} */
</style>