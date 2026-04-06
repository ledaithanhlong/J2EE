import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const formatVND = (amount) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount || 0);

const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
};

const isExpired = (expiryDate) => expiryDate && new Date(expiryDate) < new Date();

// ─────────────────────────────────────── Empty Form ───────────────────────────
const EMPTY_FORM = {
  code: '',
  description: '',
  discountPercent: '',
  discountAmount: '',
  minSpend: '',
  maxDiscount: '',
  startDate: '',
  expiryDate: '',
  usageLimit: '',
  isActive: true,
};

// ─────────────────────────────────────── Modal Form ───────────────────────────
function VoucherModal({ voucher, onClose, onSave }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  // Pre-fill
  useEffect(() => {
    if (voucher) {
      setForm({
        code: voucher.code || '',
        description: voucher.description || '',
        discountPercent: voucher.discount_percent ?? '',
        discountAmount: voucher.discount_amount ?? '',
        minSpend: voucher.min_spend ?? '',
        maxDiscount: voucher.max_discount ?? '',
        startDate: voucher.start_date ? toInputDate(voucher.start_date) : '',
        expiryDate: voucher.expiry_date ? toInputDate(voucher.expiry_date) : '',
        usageLimit: voucher.usage_limit ?? '',
        isActive: voucher.is_active !== false,
      });
    } else {
      setForm(EMPTY_FORM);
    }
  }, [voucher]);

  const toInputDate = (iso) => {
    if (!iso) return '';
    return new Date(iso).toISOString().split('T')[0];
  };

  const handleChange = (field) => (e) => {
    const val = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm(prev => ({ ...prev, [field]: val }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.code.trim()) {
      setError('Vui lòng nhập mã voucher');
      return;
    }
    if (!form.discountPercent && !form.discountAmount) {
      setError('Vui lòng nhập mức giảm giá (% hoặc số tiền)');
      return;
    }

    setSaving(true);
    try {
      const payload = {
        code: form.code.trim().toUpperCase(),
        description: form.description || null,
        discountPercent: form.discountPercent !== '' ? Number(form.discountPercent) : null,
        discountAmount: form.discountAmount !== '' ? Number(form.discountAmount) : null,
        minSpend: form.minSpend !== '' ? Number(form.minSpend) : null,
        maxDiscount: form.maxDiscount !== '' ? Number(form.maxDiscount) : null,
        startDate: form.startDate ? new Date(form.startDate).toISOString() : null,
        expiryDate: form.expiryDate ? new Date(form.expiryDate).toISOString() : null,
        usageLimit: form.usageLimit !== '' ? Number(form.usageLimit) : null,
        isActive: form.isActive,
      };

      if (voucher?.id) {
        await axios.put(`${API}/vouchers/${voucher.id}`, payload);
      } else {
        await axios.post(`${API}/vouchers`, payload);
      }
      onSave();
    } catch (err) {
      const msg = err.response?.data?.error || err.message || 'Lỗi không xác định';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-xl rounded-3xl bg-white shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff] px-6 py-5 flex items-center justify-between">
          <h2 className="text-white font-bold text-lg">
            {voucher?.id ? 'Chỉnh sửa Voucher' : 'Tạo Voucher mới'}
          </h2>
          <button onClick={onClose} className="text-white/70 hover:text-white transition">
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4 max-h-[75vh] overflow-y-auto">
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-xl">
              {error}
            </div>
          )}

          {/* Code */}
          <div>
            <label className="text-sm font-semibold text-gray-700 block mb-1">Mã Voucher *</label>
            <input
              value={form.code}
              onChange={handleChange('code')}
              placeholder="VD: SUMMER25"
              className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm font-mono uppercase focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              required
            />
          </div>

          {/* Description */}
          <div>
            <label className="text-sm font-semibold text-gray-700 block mb-1">Mô tả</label>
            <input
              value={form.description}
              onChange={handleChange('description')}
              placeholder="VD: Giảm 20% mùa hè 2025"
              className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
            />
          </div>

          {/* Discount */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Giảm % (nếu có)</label>
              <input
                type="number"
                min="0" max="100"
                value={form.discountPercent}
                onChange={handleChange('discountPercent')}
                placeholder="VD: 20"
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Giảm tiền cố định (VND)</label>
              <input
                type="number"
                min="0"
                value={form.discountAmount}
                onChange={handleChange('discountAmount')}
                placeholder="VD: 100000"
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
          </div>
          <p className="text-xs text-gray-400">* Nếu điền cả hai, ưu tiên giảm theo %</p>

          {/* Min spend & Max discount */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Đơn tối thiểu (VND)</label>
              <input
                type="number" min="0"
                value={form.minSpend}
                onChange={handleChange('minSpend')}
                placeholder="VD: 500000"
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Giảm tối đa (VND)</label>
              <input
                type="number" min="0"
                value={form.maxDiscount}
                onChange={handleChange('maxDiscount')}
                placeholder="VD: 200000"
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
          </div>

          {/* Dates */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Ngày bắt đầu</label>
              <input type="date"
                value={form.startDate}
                onChange={handleChange('startDate')}
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
            <div>
              <label className="text-sm font-semibold text-gray-700 block mb-1">Ngày hết hạn</label>
              <input type="date"
                value={form.expiryDate}
                onChange={handleChange('expiryDate')}
                className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
              />
            </div>
          </div>

          {/* Usage limit */}
          <div>
            <label className="text-sm font-semibold text-gray-700 block mb-1">Số lượt sử dụng (0 = không giới hạn)</label>
            <input
              type="number" min="0"
              value={form.usageLimit}
              onChange={handleChange('usageLimit')}
              placeholder="VD: 100"
              className="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none"
            />
          </div>

          {/* Active toggle */}
          <label className="flex items-center gap-3 cursor-pointer">
            <input
              type="checkbox"
              checked={form.isActive}
              onChange={handleChange('isActive')}
              className="w-5 h-5 rounded border-gray-300 text-blue-600"
            />
            <span className="text-sm font-semibold text-gray-700">Kích hoạt voucher này</span>
          </label>

          {/* Submit */}
          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border border-gray-200 rounded-xl py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-50 transition"
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={saving}
              className="flex-1 bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff] text-white rounded-xl py-2.5 text-sm font-semibold
                  hover:shadow-lg transition disabled:opacity-60"
            >
              {saving ? 'Đang lưu...' : voucher?.id ? 'Cập nhật' : 'Tạo mới'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ─────────────────────────────────────── Main Page ───────────────────────────
export default function AdminVouchersPage() {
  const [vouchers, setVouchers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [toast, setToast] = useState('');
  const [search, setSearch] = useState('');

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(''), 3000);
  };

  const fetchVouchers = async () => {
    setLoading(true);
    try {
      const res = await axios.get(`${API}/vouchers`);
      setVouchers(res.data);
    } catch {
      showToast('Lỗi tải danh sách voucher');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchVouchers(); }, []);

  const handleCreate = () => { setEditTarget(null); setShowModal(true); };
  const handleEdit = (v) => { setEditTarget(v); setShowModal(true); };
  const handleModalSave = () => {
    setShowModal(false);
    fetchVouchers();
    showToast(editTarget?.id ? 'Cập nhật thành công!' : 'Tạo voucher thành công!');
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await axios.delete(`${API}/vouchers/${deleteTarget.id}`);
      setDeleteTarget(null);
      fetchVouchers();
      showToast('Đã xóa voucher');
    } catch {
      showToast('Lỗi xóa voucher');
    } finally {
      setDeleting(false);
    }
  };

  const filtered = vouchers.filter(v =>
    v.code?.toLowerCase().includes(search.toLowerCase()) ||
    v.description?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-[#f8faff] py-10 px-4">

      {/* Toast */}
      {toast && (
        <div className="fixed top-6 right-6 z-50 bg-[#0A4EC3] text-white text-sm font-semibold
            px-5 py-3 rounded-2xl shadow-xl flex items-center gap-2">
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
          </svg>
          {toast}
        </div>
      )}

      <div className="max-w-6xl mx-auto">

        {/* Header */}
        <div className="mb-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <h1 className="text-3xl font-extrabold text-[#0A4EC3] tracking-tight">Quản lý Voucher</h1>
            <p className="text-gray-500 text-sm mt-1">Tạo, chỉnh sửa và xóa mã giảm giá Jurni</p>
          </div>
          <button
            onClick={handleCreate}
            className="inline-flex items-center gap-2 bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff]
                text-white font-bold px-5 py-3 rounded-2xl shadow-lg hover:shadow-xl transition-all
                hover:-translate-y-0.5 active:scale-95"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" />
            </svg>
            Tạo Voucher mới
          </button>
        </div>

        {/* Search */}
        <div className="mb-6 relative max-w-sm">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Tìm theo mã hoặc mô tả..."
            className="w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-xl text-sm focus:border-[#0A4EC3] focus:ring-2 focus:ring-blue-100 outline-none bg-white"
          />
        </div>

        {/* Stats row */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Tổng voucher', value: vouchers.length, color: 'text-blue-700', bg: 'bg-blue-50' },
            {
              label: 'Đang kích hoạt',
              value: vouchers.filter(v => v.is_active && !isExpired(v.expiry_date)).length,
              color: 'text-emerald-700', bg: 'bg-emerald-50'
            },
            {
              label: 'Đã hết hạn',
              value: vouchers.filter(v => isExpired(v.expiry_date)).length,
              color: 'text-red-700', bg: 'bg-red-50'
            },
            {
              label: 'Vô hiệu hóa',
              value: vouchers.filter(v => !v.is_active).length,
              color: 'text-gray-700', bg: 'bg-gray-100'
            },
          ].map(stat => (
            <div key={stat.label} className={`${stat.bg} rounded-2xl px-5 py-4 shadow-sm`}>
              <p className="text-xs text-gray-500 mb-1">{stat.label}</p>
              <p className={`text-2xl font-extrabold ${stat.color}`}>{stat.value}</p>
            </div>
          ))}
        </div>

        {/* Table */}
        <div className="bg-white rounded-3xl shadow-sm border border-blue-50 overflow-hidden">
          {loading ? (
            <div className="flex justify-center items-center py-20">
              <div className="animate-spin rounded-full h-10 w-10 border-4 border-blue-200 border-t-[#0A4EC3]" />
            </div>
          ) : filtered.length === 0 ? (
            <div className="text-center py-20 text-gray-400">
              <div className="text-5xl mb-3">🎫</div>
              <p className="font-medium">{vouchers.length === 0 ? 'Chưa có voucher nào' : 'Không tìm thấy kết quả'}</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff] text-white">
                    {['Mã', 'Mô tả', 'Giảm giá', 'Đơn tối thiểu', 'Lượt dùng', 'Hết hạn', 'Trạng thái', ''].map(h => (
                      <th key={h} className="px-4 py-3 text-left font-semibold text-xs uppercase tracking-wide whitespace-nowrap">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((v, idx) => {
                    const expired = isExpired(v.expiry_date);
                    const active = v.is_active && !expired;
                    return (
                      <tr
                        key={v.id}
                        className={`border-b border-gray-50 transition-colors
                          ${idx % 2 === 0 ? 'bg-white' : 'bg-blue-50/20'}
                          hover:bg-blue-50/50`}
                      >
                        <td className="px-4 py-3">
                          <span className="font-mono font-bold text-[#0A4EC3] tracking-widest bg-blue-50 px-2 py-0.5 rounded-lg text-xs">
                            {v.code}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-gray-600 max-w-[140px] truncate">
                          {v.description || '—'}
                        </td>
                        <td className="px-4 py-3 font-bold text-gray-800 whitespace-nowrap">
                          {(v.discount_percent ?? 0) > 0
                            ? <span className="text-[#0A4EC3]">-{v.discount_percent}%</span>
                            : <span className="text-emerald-600">-{formatVND(v.discount_amount)}</span>
                          }
                          {(v.max_discount ?? 0) > 0 && (
                            <span className="text-xs text-gray-400 ml-1">(tối đa {formatVND(v.max_discount)})</span>
                          )}
                        </td>
                        <td className="px-4 py-3 text-gray-500 whitespace-nowrap">
                          {(v.min_spend ?? 0) > 0 ? formatVND(v.min_spend) : '—'}
                        </td>
                        <td className="px-4 py-3 text-gray-500 whitespace-nowrap">
                          {(v.current_usage ?? 0)}
                          {(v.usage_limit ?? 0) > 0 ? `/${v.usage_limit}` : ' / ∞'}
                        </td>
                        <td className="px-4 py-3 text-gray-500 whitespace-nowrap">
                          {formatDate(v.expiry_date)}
                          {expired && <span className="ml-1 text-red-400 text-xs">(hết)</span>}
                        </td>
                        <td className="px-4 py-3">
                          <span className={`inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full
                            ${active
                              ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                              : expired
                                ? 'bg-red-50 text-red-500 border border-red-200'
                                : 'bg-gray-100 text-gray-500 border border-gray-200'
                            }`}>
                            <span className={`w-1.5 h-1.5 rounded-full ${active ? 'bg-emerald-500' : 'bg-gray-400'}`} />
                            {active ? 'Đang diễn ra' : expired ? 'Hết hạn' : 'Vô hiệu'}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <div className="flex items-center gap-2">
                            <button
                              onClick={() => handleEdit(v)}
                              className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition"
                              title="Chỉnh sửa"
                            >
                              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                  d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                              </svg>
                            </button>
                            <button
                              onClick={() => setDeleteTarget(v)}
                              className="p-1.5 text-red-500 hover:bg-red-50 rounded-lg transition"
                              title="Xóa"
                            >
                              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                              </svg>
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Voucher Modal */}
      {showModal && (
        <VoucherModal
          voucher={editTarget}
          onClose={() => setShowModal(false)}
          onSave={handleModalSave}
        />
      )}

      {/* Delete Confirm Modal */}
      {deleteTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white rounded-3xl p-6 max-w-sm w-full shadow-2xl text-center">
            <div className="w-14 h-14 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-7 h-7 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <h3 className="font-bold text-gray-900 text-lg mb-2">Xác nhận xóa?</h3>
            <p className="text-gray-500 text-sm mb-6">
              Bạn muốn xóa voucher <b className="text-red-500">{deleteTarget.code}</b>?
              Hành động này không thể hoàn tác.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setDeleteTarget(null)}
                className="flex-1 border border-gray-200 rounded-xl py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-50 transition"
              >
                Hủy
              </button>
              <button
                onClick={handleDelete}
                disabled={deleting}
                className="flex-1 bg-red-500 text-white rounded-xl py-2.5 text-sm font-semibold hover:bg-red-600 transition disabled:opacity-60"
              >
                {deleting ? 'Đang xóa...' : 'Xóa'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
