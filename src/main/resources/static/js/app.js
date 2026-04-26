const API_BASE = 'http://localhost:8080';

let ws = null;
let authToken = localStorage.getItem('authToken') || '';
let tokenType = localStorage.getItem('tokenType') || '';

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initAuthForms();
    updateLoginStatus();
});

function initNavigation() {
    const menuItems = document.querySelectorAll('.menu-item');
    const pages = document.querySelectorAll('.page');
    const pageTitle = document.getElementById('page-title');

    menuItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            menuItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            const pageName = item.dataset.page;
            pages.forEach(page => page.classList.remove('active'));
            document.getElementById(`${pageName}-page`).classList.add('active');
            pageTitle.textContent = item.querySelector('span:last-child').textContent;
        });
    });
}

function initAuthForms() {
    const adminForm = document.getElementById('admin-login-form');
    if(adminForm) adminForm.addEventListener('submit', async (e) => { e.preventDefault(); await login(); });

    const logoutBtn = document.getElementById('logout-btn');
    if(logoutBtn) logoutBtn.addEventListener('click', logout);
}

async function login() {
    const username = document.getElementById('admin-username').value;
    const password = document.getElementById('admin-password').value;

    try {
        const url = `${API_BASE}/admin/employee/login`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const result = await response.json();
        if (result.code === 1) {
            authToken = result.data.token;

            // 优先取 name，如果没有取 username，最后取管理员
            const displayName = result.data.name || result.data.username || '管理员';

            // 强制覆盖旧缓存
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('displayName', displayName);

            showNotification('登录成功！', 'success');
            updateLoginStatus();
            displayTokenInfo(result.data);
        } else {
            showNotification(result.msg || '登录失败', 'error');
        }
    } catch (error) {
        showNotification('网络错误: ' + error.message, 'error');
    }
}

function logout() {
    authToken = '';
    localStorage.removeItem('authToken');
    localStorage.removeItem('displayName');
    updateLoginStatus();
    showNotification('已退出登录', 'success');
}

function updateLoginStatus() {
    const userStatus = document.getElementById('user-status');
    const logoutBtn = document.getElementById('logout-btn');
    const displayName = localStorage.getItem('displayName');

    if (authToken) {
        userStatus.textContent = `已登录 (${displayName})`;
        logoutBtn.style.display = 'block';
    } else {
        userStatus.textContent = '未登录';
        logoutBtn.style.display = 'none';
    }
}

function displayTokenInfo(data) {
    const tokenInfo = document.getElementById('token-info');
    if(tokenInfo) {
        tokenInfo.innerHTML = `
            <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-top: 10px;">
                <p><strong>Token:</strong></p>
                <code style="word-break: break-all;">${data.token}</code>
                <p style="margin-top: 10px;"><strong>ID:</strong> ${data.id}</p>
            </div>`;
    }
}

function getAuthHeaders() {
    const headers = {};
    if (authToken) headers['Authorization'] = authToken;
    return headers;
}

async function redisSet() {
    const key = document.getElementById('redis-key').value;
    const value = document.getElementById('redis-value').value;
    if (!key || !value) return showNotification('请输入Key和Value', 'error');

    try {
        const response = await fetch(`${API_BASE}/admin/redis/set`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
            body: JSON.stringify({ key, value })
        });
        const result = await response.json();
        displayResult('redis-result', result);
        showNotification('设置成功', 'success');
    } catch (error) {
        showNotification('操作失败: ' + error.message, 'error');
    }
}

async function redisGet() {
    const key = document.getElementById('redis-key').value;
    if (!key) return showNotification('请输入Key', 'error');
    try {
        const response = await fetch(`${API_BASE}/admin/redis/get/${key}`, { headers: getAuthHeaders() });
        const result = await response.json();
        displayResult('redis-result', result);
    } catch (error) {
        showNotification('操作失败: ' + error.message, 'error');
    }
}

async function redisDelete() {
    const key = document.getElementById('redis-key').value;
    if (!key) return showNotification('请输入Key', 'error');
    try {
        const response = await fetch(`${API_BASE}/admin/redis/delete/${key}`, {
            method: 'DELETE', headers: getAuthHeaders()
        });
        const result = await response.json();
        displayResult('redis-result', result);
        showNotification('删除成功', 'success');
    } catch (error) {
        showNotification('操作失败: ' + error.message, 'error');
    }
}

async function loadProductNativeCache() {
    try {
        const response = await fetch(`${API_BASE}/admin/redis/product/native`, { headers: getAuthHeaders() });
        const result = await response.json();
        renderProductCacheResult('product-native-meta', 'product-native-table', result);
        showNotification('Redis 原生缓存加载成功', 'success');
    } catch (error) {
        showNotification('加载失败: ' + error.message, 'error');
    }
}

async function clearProductNativeCache() {
    try {
        const response = await fetch(`${API_BASE}/admin/redis/product/native/clear`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        const result = await response.json();
        document.getElementById('product-native-meta').textContent = result.message || '已清理';
        document.getElementById('product-native-table').innerHTML = '';
        showNotification('Redis 原生缓存已清理', 'success');
    } catch (error) {
        showNotification('清理失败: ' + error.message, 'error');
    }
}

async function loadProductSpringCache() {
    try {
        const response = await fetch(`${API_BASE}/admin/redis/product/springcache`, { headers: getAuthHeaders() });
        const result = await response.json();
        renderProductCacheResult('product-spring-meta', 'product-spring-table', result);
        showNotification('SpringCache 缓存加载成功', 'success');
    } catch (error) {
        showNotification('加载失败: ' + error.message, 'error');
    }
}

async function clearProductSpringCache() {
    try {
        const response = await fetch(`${API_BASE}/admin/redis/product/springcache/clear`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        const result = await response.json();
        document.getElementById('product-spring-meta').textContent = result.message || '已清理';
        document.getElementById('product-spring-table').innerHTML = '';
        showNotification('SpringCache 缓存已清理', 'success');
    } catch (error) {
        showNotification('清理失败: ' + error.message, 'error');
    }
}

function renderProductCacheResult(metaId, tableId, result) {
    const meta = document.getElementById(metaId);
    const tableWrap = document.getElementById(tableId);
    const products = result.data || [];

    meta.textContent = `来源: ${result.source || 'unknown'} | 数量: ${result.count ?? products.length}`;

    if (!products.length) {
        tableWrap.innerHTML = '<div class="cache-meta">暂无商品数据，请先确认 product 表中有记录</div>';
        return;
    }

    const rows = products.map(item => `
        <tr>
            <td>${item.id ?? ''}</td>
            <td>${item.name ?? ''}</td>
            <td>${item.categoryId ?? ''}</td>
            <td>${item.price ?? ''}</td>
            <td>${item.status ?? ''}</td>
        </tr>
    `).join('');

    tableWrap.innerHTML = `
        <table class="product-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>名称</th>
                    <th>分类ID</th>
                    <th>价格</th>
                    <th>状态</th>
                </tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>
    `;
}

function connectWebSocket() {
    const wsUrl = 'ws://localhost:8080/ws/message';
    ws = new WebSocket(wsUrl);

    ws.onopen = function() {
        document.getElementById('ws-status').textContent = '已连接';
        document.getElementById('ws-status').classList.add('connected');
        showNotification('WebSocket连接成功', 'success');
    };

    ws.onmessage = function(event) {
        addMessage('收到: ' + event.data);
    };

    ws.onerror = function() {
        showNotification('WebSocket错误', 'error');
    };

    ws.onclose = function() {
        document.getElementById('ws-status').textContent = '未连接';
        document.getElementById('ws-status').classList.remove('connected');
    };
}

function disconnectWebSocket() {
    if (ws) { ws.close(); ws = null; }
}

function sendWebSocketMessage() {
    if (!ws || ws.readyState !== WebSocket.OPEN) return showNotification('请先连接WebSocket', 'error');
    const message = document.getElementById('ws-message').value;
    if (message) {
        ws.send(message);
        addMessage('发送: ' + message);
        document.getElementById('ws-message').value = '';
    }
}

function addMessage(message) {
    const messageBox = document.getElementById('ws-messages');
    const time = new Date().toLocaleTimeString();
    messageBox.innerHTML += `<div>[${time}] ${message}</div>`;
    messageBox.scrollTop = messageBox.scrollHeight;
}

function uploadFiles() {
    const fileInput = document.getElementById('file-input');
    const files = fileInput.files;
    if (files.length === 0) return showNotification('请选择文件', 'error');

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    fetch(`${API_BASE}/admin/common/upload`, {
        method: 'POST',
        headers: getAuthHeaders(), // 不要加 Content-Type
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error('上传失败，状态码: ' + response.status);
        return response.json();
    })
    .then(result => {
        displayResult('upload-result', result);
        showNotification('上传成功', 'success');
    })
    .catch(error => {
        showNotification('上传失败: ' + error.message, 'error');
    });
}

function generateOrderNumber() {
    const orderNumber = 'ORD' + Date.now() + Math.random().toString(36).substr(2, 9).toUpperCase();
    document.getElementById('order-number').value = orderNumber;
}

async function createPayment() {
    const orderNumber = document.getElementById('order-number').value;
    const amount = document.getElementById('payment-amount').value;
    if (!orderNumber) return showNotification('请输入订单号', 'error');

    try {
        const response = await fetch(`${API_BASE}/user/payment/pay`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
            body: JSON.stringify({ orderNumber, amount })
        });
        const result = await response.json();
        displayResult('payment-result', result);
        if (result.code === 1 && result.data.qrcode) {
            document.getElementById('payment-qrcode').innerHTML = `<img src="${result.data.qrcode}" alt="二维码" style="max-width: 300px;">`;
        }
    } catch (error) {
        showNotification('支付失败: ' + error.message, 'error');
    }
}

function displayResult(elementId, data) {
    const element = document.getElementById(elementId);
    if(element) element.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed; top: 20px; right: 20px; padding: 15px 25px;
        background: ${type === 'success' ? '#2ed573' : type === 'error' ? '#ff4757' : '#667eea'};
        color: white; border-radius: 8px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        z-index: 1000; animation: slideIn 0.3s ease;`;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}

const uploadArea = document.getElementById('upload-area');
if (uploadArea) {
    uploadArea.addEventListener('click', () => document.getElementById('file-input').click());
    uploadArea.addEventListener('dragover', (e) => { e.preventDefault(); uploadArea.style.background = '#f0f0f0'; });
    uploadArea.addEventListener('dragleave', () => { uploadArea.style.background = ''; });
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault(); uploadArea.style.background = '';
        const files = e.dataTransfer.files;
        document.getElementById('file-input').files = files;
    });
}
