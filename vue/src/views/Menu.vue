<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" v-model="name" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd(null)">新增 <el-icon><Plus /></el-icon></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          title="您确定批量删除这些数据吗?"
          @confirm="delBatch"
      >
        <template #reference>
          <el-button type="danger">批量删除 <el-icon><Delete /></el-icon></el-button>
        </template>
      </el-popconfirm>
      <el-button class="ml-5" :type="sortOrder === 'asc' ? 'success' : 'info'" @click="sortById('asc')">按ID升序</el-button>
      <el-button :type="sortOrder === 'desc' ? 'success' : 'info'" @click="sortById('desc')">按ID降序</el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="pid" label="父级ID" width="80"></el-table-column>
      <el-table-column prop="path" label="路径"></el-table-column>
      <el-table-column prop="pagePath" label="页面路径"></el-table-column>
      <el-table-column label="图标" class-name="fontSize18" align="center" label-class-name="fontSize12">
        <template #default="scope">
          <template v-if="scope.row.icon && typeof scope.row.icon === 'string' && scope.row.icon.trim()">
            <el-icon :size="20"><component :is="getIconComponent(scope.row.icon.trim())" /></el-icon>
          </template>
          <el-icon v-else :size="20"><MenuIcon /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="sortNum" label="顺序"></el-table-column>
      <el-table-column label="操作"  width="300" align="center">
        <template #default="scope">
          <el-button type="primary" @click="handleAdd(scope.row.id)" v-if="!scope.row.pid && !scope.row.path">新增子菜单 <el-icon><Plus /></el-icon></el-button>
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <el-icon><Edit /></el-icon></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              title="您确定删除吗?"
              @confirm="del(scope.row.id)">
            <template #reference>
              <el-button type="danger">删除 <el-icon><Delete /></el-icon></el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="菜单信息" v-model="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="页面路径">
          <el-input v-model="form.pagePath" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-select clearable v-model="form.icon" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in options" :key="item.name" :label="item.name" :value="item.value">
              <el-icon><component :is="getIconComponent(item.value)" /></el-icon> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="顺序">
          <el-input v-model="form.sortNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Search, Plus, Delete, Edit, Coffee, Document, House, User, Grid, Upload, Download, Message, ArrowDown, Loading, Menu as MenuIcon, Check, Close, Top, Bottom, Warning, Fold, Expand, DataAnalysis, PieChart, Setting, ChatDotRound, Tools, List, VideoPlay, OfficeBuilding, DocumentCopy, Reading, EditPen, Folder, FolderOpened, DataLine, Avatar, Cpu, InfoFilled, Odometer } from '@element-plus/icons-vue';

export default {
  name: "Menu",
  components: { Search, Plus, Delete, Edit, Coffee, Document, House, User, Grid, Upload, Download, Message, ArrowDown, Loading, MenuIcon, Check, Close, Top, Bottom, Warning, Fold, Expand, DataAnalysis, PieChart, Setting, ChatDotRound, Tools, List, VideoPlay, OfficeBuilding, DocumentCopy, Reading, EditPen, Folder, FolderOpened, DataLine, Avatar, Cpu, InfoFilled, Odometer },
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      sortBy: "id",
      sortOrder: "asc",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      options: [],
      iconLoading: false
    }
  },
  created() {
    this.load()
    this.loadIcons()
  },
  methods: {
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
        'el-icon-question': 'MenuIcon',
        'el-icon-time': 'MenuIcon',
        'el-icon-circle-check': 'Check',
        'el-icon-close': 'Close',
        'el-icon-top': 'Top',
        'el-icon-bottom': 'Bottom',
        'el-icon-warning': 'Warning',
        'el-icon-s-fold': 'Fold',
        'el-icon-s-unfold': 'Expand',
        'el-icon-data-analysis': 'DataAnalysis',
        'el-icon-dashboard': 'Odometer',
        'el-icon-pie-chart': 'PieChart',
        'el-icon-setting': 'Setting',
        'el-icon-chat-dot-round': 'ChatDotRound',
        'el-icon-tools': 'Tools',
        'el-icon-list': 'List',
        'el-icon-video-play': 'VideoPlay',
        'el-icon-office-building': 'OfficeBuilding',
        'el-icon-document-copy': 'DocumentCopy',
        'el-icon-reading': 'Reading',
        'el-icon-edit-pen': 'EditPen',
        'el-icon-folder': 'Folder',
        'el-icon-folder-opened': 'FolderOpened',
        'el-icon-data-line': 'DataLine',
        'el-icon-s-custom': 'Avatar',
        'el-icon-cpu': 'Cpu',
        'el-icon-menu': 'MenuIcon',
        'el-icon-s-data': 'DataLine',
        'el-icon-info': 'InfoFilled',
        'el-icon-s-marketing': 'DataAnalysis',
        'el-icon-alert': 'Warning'
      };
      return iconMap[iconName] || 'MenuIcon';
    },
    load() {
      console.log('load方法被调用，sortBy:', this.sortBy, 'sortOrder:', this.sortOrder)
      request.get("/menu/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          sortBy: this.sortBy,
          sortOrder: this.sortOrder
        }
      }).then(res => {
        console.log('后端返回数据:', res)
        if (res.code === '200') {
          this.tableData = res.data?.records || []
          this.total = res.data?.total || 0
          console.log('当前表格数据:', this.tableData)
        }
      }).catch(err => {
        console.error('加载菜单列表失败:', err)
      })
    },
    sortById(order) {
      console.log('sortById被调用，order:', order)
      this.sortBy = "id"
      this.sortOrder = order
      this.pageNum = 1
      this.load()
    },
    loadIcons() {
      if (this.options.length > 0 || this.iconLoading) return
      this.iconLoading = true

      request.get("/menu/icons")
        .then(res => {
          this.options = res.data || []
        })
        .catch(() => {
          console.warn('⚠️ 后端 icons 接口异常，使用本地兜底图标')
          this.options = [
            {name: '主页', value: 'el-icon-house'},
            {name: '设置', value: 'el-icon-setting'},
            {name: '用户', value: 'el-icon-user'},
            {name: '网格', value: 'el-icon-s-grid'},
            {name: '分析', value: 'el-icon-data-analysis'},
            {name: 'CPU', value: 'el-icon-cpu'},
            {name: '文件夹', value: 'el-icon-folder-opened'},
            {name: '上传', value: 'el-icon-upload'},
            {name: '工具', value: 'el-icon-tools'},
            {name: '聊天', value: 'el-icon-chat-dot-round'},
            {name: '阅读', value: 'el-icon-reading'},
            {name: '编辑', value: 'el-icon-edit-pen'},
            {name: '警告', value: 'el-icon-warning'},
            {name: '列表', value: 'el-icon-list'},
            {name: '视频', value: 'el-icon-video-play'},
            {name: '建筑', value: 'el-icon-office-building'},
            {name: '复制', value: 'el-icon-document-copy'},
            {name: '饼图', value: 'el-icon-pie-chart'},
            {name: '数据', value: 'el-icon-data-line'},
            {name: '头像', value: 'el-icon-s-custom'}
          ]
        })
        .finally(() => {
          this.iconLoading = false
        })
    },
    save() {
      request.post("/menu", this.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          ElMessage.error("保存失败")
        }
      })
    },
    handleAdd(pid) {
      this.dialogFormVisible = true
      this.form = {}
      if (pid) {
        this.form.pid = pid
      }
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      request.delete("/menu/" + id).then(res => {
        if (res.code === '200') {
          ElMessage.success("删除成功")
          this.load()
        } else {
          ElMessage.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)
      request.post("/menu/del/batch", ids).then(res => {
        if (res.code === '200') {
          ElMessage.success("批量删除成功")
          this.load()
        } else {
          ElMessage.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.sortBy = "id"
      this.sortOrder = "asc"
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee !important;
}

.fontSize18 {
  font-size: 18px;
}

.fontSize12 {
  font-size: 12px;
}
</style>
