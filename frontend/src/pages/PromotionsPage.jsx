import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const formatVND = (amount) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
};

const getTimeLeft = (expiryDate) => {
  if (!expiryDate) return null;
  const diff = new Date(expiryDate) - new Date();
  if (diff <= 0) return { expired: true };
  const days = Math.floor(diff / 86400000);
  const hours = Math.floor((diff % 86400000) / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  return { days, hours, minutes, expired: false };
};

// ────────────────────────────────────── Voucher Card ──────────────────────────
function VoucherCard({ voucher, onCopy }) {
  const [copied, setCopied] = useState(false);
  const [timeLeft, setTimeLeft] = useState(getTimeLeft(voucher.expiry_date));

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(getTimeLeft(voucher.expiry_date));
    }, 60000);
    return () => clearInterval(timer);
  }, [voucher.expiry_date]);

  const isPercent = (voucher.discount_percent ?? 0) > 0;
  const discountLabel = isPercent
    ? `Giảm ${voucher.discount_percent}%`
    : `Giảm ${formatVND(voucher.discount_amount)}`;

  const maxDiscountNote = isPercent && voucher.max_discount > 0
    ? `Tối đa ${formatVND(voucher.max_discount)}`
    : null;

  const minSpendNote = (voucher.min_spend ?? 0) > 0
    ? `Cho đơn từ ${formatVND(voucher.min_spend)}`
    : 'Không giới hạn đơn tối thiểu';

  const handleCopy = () => {
    navigator.clipboard.writeText(voucher.code).catch(() => {});
    setCopied(true);
    onCopy(voucher.code);
    setTimeout(() => setCopied(false), 2000);
  };

  const isExpired = timeLeft?.expired;
  const isExpiringSoon = timeLeft && !timeLeft.expired && timeLeft.days <= 3;

  return (
    <div
      className={`relative flex rounded-2xl overflow-hidden shadow-lg transition-all duration-300 group
        ${isExpired ? 'opacity-60 grayscale' : 'hover:shadow-2xl hover:-translate-y-1'}`}
    >
      {/* Left color strip */}
      <div className="w-2 flex-shrink-0 bg-gradient-to-b from-[#0A4EC3] to-[#1e6aff]" />

      {/* Main card body */}
      <div className="flex-1 bg-white border border-l-0 border-[#e5eeff] rounded-r-2xl flex flex-col">

        {/* Top section */}
        <div className="px-5 pt-5 pb-3 flex items-start justify-between gap-3">
          <div className="flex-1">
            {/* Discount badge */}
            <div className="inline-flex items-center gap-1.5 bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff]
                text-white text-xs font-bold px-3 py-1 rounded-full mb-2 shadow-sm">
              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5}
                  d="M7 7h.01M17 17h.01M7 17L17 7M9.5 4.5L4.5 9.5a2.121 2.121 0 000 3l7 7a2.121 2.121 0 003 0l5-5a2.121 2.121 0 000-3l-7-7a2.121 2.121 0 00-3 0z" />
              </svg>
              {discountLabel}
            </div>
            {maxDiscountNote && (
              <p className="text-xs text-[#0A4EC3] font-semibold mb-1">{maxDiscountNote}</p>
            )}
            <h3 className="font-bold text-gray-800 text-sm leading-snug mb-0.5">
              {voucher.description || `Ưu đãi ${discountLabel}`}
            </h3>
            <p className="text-xs text-gray-500">{minSpendNote}</p>
          </div>

          {/* Status tag */}
          {isExpired ? (
            <span className="text-xs bg-gray-100 text-gray-400 px-2 py-1 rounded-full font-medium whitespace-nowrap">
              Hết hạn
            </span>
          ) : isExpiringSoon ? (
            <span className="text-xs bg-red-50 text-red-500 border border-red-200 px-2 py-1 rounded-full font-semibold animate-pulse whitespace-nowrap">
              Sắp hết
            </span>
          ) : (
            <span className="text-xs bg-emerald-50 text-emerald-600 border border-emerald-200 px-2 py-1 rounded-full font-medium whitespace-nowrap">
              Đang diễn ra
            </span>
          )}
        </div>

        {/* Dashed divider (coupon tear) */}
        <div className="mx-4 border-t border-dashed border-gray-200 my-1" />

        {/* Expiry and countdown */}
        <div className="px-5 py-2 flex items-center justify-between text-xs text-gray-400">
          <span>HSD: {formatDate(voucher.expiry_date)}</span>
          {!isExpired && timeLeft && (
            <span className={`font-semibold ${isExpiringSoon ? 'text-red-500' : 'text-gray-500'}`}>
              Còn {timeLeft.days > 0 ? `${timeLeft.days}n ` : ''}{timeLeft.hours}h {timeLeft.minutes}p
            </span>
          )}
          {voucher.usage_limit > 0 && (
            <span className="text-gray-400">
              Đã dùng: {voucher.current_usage ?? 0}/{voucher.usage_limit}
            </span>
          )}
        </div>

        {/* Code + copy button */}
        <div className="px-5 pb-5">
          <div className="flex items-center gap-2">
            <div className="flex-1 bg-[#f0f6ff] border border-dashed border-[#93c5fd] rounded-xl
                px-3 py-2 font-mono text-sm text-[#0A4EC3] font-bold tracking-widest text-center truncate">
              {voucher.code}
            </div>
            <button
              onClick={handleCopy}
              disabled={isExpired}
              className={`flex items-center gap-1.5 px-4 py-2 rounded-xl text-sm font-semibold transition-all duration-200 shadow-sm
                ${copied
                  ? 'bg-emerald-500 text-white scale-95'
                  : isExpired
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff] text-white hover:shadow-md active:scale-95'
                }`}
            >
              {copied ? (
                <>
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
                  </svg>
                  Đã sao chép
                </>
              ) : (
                <>
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M8 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3" />
                  </svg>
                  Sao chép
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ────────────────────────────────────── Skeleton Loader ───────────────────────
function SkeletonCard() {
  return (
    <div className="relative flex rounded-2xl overflow-hidden shadow animate-pulse">
      <div className="w-2 bg-blue-200 flex-shrink-0" />
      <div className="flex-1 bg-white border border-l-0 border-blue-100 rounded-r-2xl p-5 space-y-3">
        <div className="h-5 bg-blue-100 rounded-full w-1/2" />
        <div className="h-4 bg-gray-100 rounded-full w-2/3" />
        <div className="h-3 bg-gray-100 rounded-full w-1/3" />
        <div className="border-t border-dashed border-gray-200" />
        <div className="h-10 bg-blue-50 rounded-xl" />
      </div>
    </div>
  );
}

// ────────────────────────────────────── Main Page ─────────────────────────────
export default function PromotionsPage() {
  const [vouchers, setVouchers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all'); // 'all' | 'percent' | 'fixed'
  const [toastMsg, setToastMsg] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchVouchers = async () => {
      try {
        const res = await axios.get(`${API}/vouchers`);
        // Show both active and expired, but sort: active first
        const sorted = [...res.data].sort((a, b) => {
          const aExp = a.expiry_date ? new Date(a.expiry_date) < new Date() : false;
          const bExp = b.expiry_date ? new Date(b.expiry_date) < new Date() : false;
          if (aExp !== bExp) return aExp ? 1 : -1;
          return new Date(b.created_at || 0) - new Date(a.created_at || 0);
        });
        setVouchers(sorted);
      } catch (error) {
        console.error('Failed to fetch vouchers', error);
      } finally {
        setLoading(false);
      }
    };
    fetchVouchers();
  }, []);

  const handleCopy = useCallback((code) => {
    setToastMsg(`Đã sao chép mã: ${code}`);
    setTimeout(() => setToastMsg(''), 2500);
  }, []);

  const filtered = vouchers.filter(v => {
    if (filter === 'percent') return (v.discount_percent ?? 0) > 0;
    if (filter === 'fixed') return (v.discount_percent ?? 0) === 0;
    return true;
  });

  const activeCount = vouchers.filter(v => {
    const now = new Date();
    return v.is_active && (!v.expiry_date || new Date(v.expiry_date) > now);
  }).length;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#f0f6ff] via-white to-white">

      {/* ── Toast notification ──────────────────────────────────── */}
      {toastMsg && (
        <div className="fixed top-6 left-1/2 -translate-x-1/2 z-50
            bg-emerald-500 text-white text-sm font-semibold px-5 py-3 rounded-2xl shadow-xl
            flex items-center gap-2 animate-bounce-in">
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
          </svg>
          {toastMsg}
        </div>
      )}

      {/* ── Hero Section ───────────────────────────────────────────── */}
      <div className="relative overflow-hidden bg-gradient-to-br from-[#0A4EC3] via-[#1555d1] to-[#0d3fa3] pt-20 pb-32">
        {/* Decorative circles */}
        <div className="absolute -top-16 -right-16 w-64 h-64 bg-white/5 rounded-full" />
        <div className="absolute -bottom-10 -left-10 w-48 h-48 bg-white/5 rounded-full" />

        <div className="relative max-w-5xl mx-auto px-4 text-center">
          <div className="inline-flex items-center gap-2 bg-white/10 backdrop-blur border border-white/20
              text-white text-xs font-semibold px-4 py-1.5 rounded-full mb-5">
            <svg className="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M5 2a2 2 0 00-2 2v14l3.5-2 3.5 2 3.5-2 3.5 2V4a2 2 0 00-2-2H5zm2.5 3a1.5 1.5 0 100 3 1.5 1.5 0 000-3zm6.207.293a1 1 0 00-1.414 0l-6 6a1 1 0 101.414 1.414l6-6a1 1 0 000-1.414zM12.5 10a1.5 1.5 0 100 3 1.5 1.5 0 000-3z" clipRule="evenodd" />
            </svg>
            {activeCount} ưu đãi đang diễn ra
          </div>

          <h1 className="text-4xl md:text-5xl font-extrabold text-white mb-4 tracking-tight">
            Kho Ưu Đãi <span className="text-[#93c5fd]">Jurni</span>
          </h1>
          <p className="text-blue-100 max-w-xl mx-auto text-base leading-relaxed">
            Săn mã giảm giá cực hot — áp dụng cho khách sạn, vé máy bay, thuê xe và tour du lịch.
            Số lượng có hạn, lưu mã và sử dụng ngay!
          </p>

          <button
            onClick={() => navigate('/payment')}
            className="mt-8 inline-flex items-center gap-2 bg-white text-[#0A4EC3] font-bold
                px-6 py-3 rounded-2xl shadow-lg hover:shadow-xl transition-all hover:-translate-y-0.5 active:scale-95"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            Đặt dịch vụ ngay
          </button>
        </div>
      </div>

      {/* ── Filter & Grid ──────────────────────────────────────────── */}
      <div className="max-w-5xl mx-auto px-4 -mt-16 pb-20">

        {/* Filter tabs */}
        <div className="flex justify-center mb-8">
          <div className="inline-flex bg-white rounded-2xl shadow-lg border border-[#e5eeff] p-1 gap-1">
            {[
              { key: 'all', label: 'Tất cả' },
              { key: 'percent', label: '% Giảm theo phần trăm' },
              { key: 'fixed', label: 'Giảm tiền cố định' },
            ].map(tab => (
              <button
                key={tab.key}
                onClick={() => setFilter(tab.key)}
                className={`px-4 py-2 rounded-xl text-sm font-semibold transition-all duration-200
                  ${filter === tab.key
                    ? 'bg-[#0A4EC3] text-white shadow-md'
                    : 'text-gray-500 hover:text-[#0A4EC3] hover:bg-blue-50'
                  }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Content */}
        {loading ? (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
            {[1, 2, 3, 4, 5, 6].map(i => <SkeletonCard key={i} />)}
          </div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-24 bg-white rounded-3xl shadow-sm border border-blue-50">
            <div className="text-5xl mb-4">🎫</div>
            <p className="text-gray-500 text-lg font-medium">
              {vouchers.length === 0
                ? 'Hiện tại chưa có mã giảm giá nào.'
                : 'Không có mã nào phù hợp với bộ lọc này.'}
            </p>
            {vouchers.length === 0 && (
              <Link to="/" className="inline-block mt-4 text-[#0A4EC3] font-semibold hover:underline">
                Quay về trang chủ
              </Link>
            )}
          </div>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
            {filtered.map(voucher => (
              <VoucherCard key={voucher.id} voucher={voucher} onCopy={handleCopy} />
            ))}
          </div>
        )}

        {/* CTA section */}
        {!loading && vouchers.length > 0 && (
          <div className="mt-12 bg-gradient-to-r from-[#0A4EC3] to-[#1e6aff] rounded-3xl p-8 text-white text-center shadow-xl">
            <h2 className="text-2xl font-bold mb-2">Sẵn sàng đặt dịch vụ?</h2>
            <p className="text-blue-100 mb-5">Sao chép mã, vào trang thanh toán và nhập vào ô "Mã ưu đãi"</p>
            <button
              onClick={() => navigate('/payment')}
              className="inline-flex items-center gap-2 bg-white text-[#0A4EC3] font-bold
                  px-6 py-3 rounded-2xl shadow hover:shadow-lg transition-all hover:-translate-y-0.5 active:scale-95"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
              Đến trang thanh toán
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
