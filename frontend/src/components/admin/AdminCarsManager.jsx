import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { useAuth } from "@clerk/clerk-react";

const API = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const emptyForm = {
  id: "",
  company: "",
  model: "",
  type: "",
  pricePerDay: "",
  seats: "5",
  location: "",
  imageUrl: "",
  available: true,
  description: "",
  engine: "",
  fuel: "",
  transmission: "",
  luggageSpace: "",
  amenitiesText: "",
};

function normalizeCar(car) {
  const specs =
    car?.specifications && typeof car.specifications === "object"
      ? car.specifications
      : {};

  return {
    id: car?.id || "",
    company: car?.company || "",
    model: car?.model || "",
    type: car?.type || "",
    pricePerDay: car?.price_per_day ?? car?.pricePerDay ?? "",
    seats: String(car?.seats ?? "5"),
    location: car?.location || "",
    imageUrl: car?.image_url || car?.imageUrl || "",
    available: car?.available ?? true,
    description: car?.description || "",
    engine: specs.engine || "",
    fuel: specs.fuel || "",
    transmission: specs.transmission || "",
    luggageSpace: specs.luggageSpace || "",
    amenitiesText: Array.isArray(car?.amenities) ? car.amenities.join(", ") : "",
  };
}

function buildPayload(form) {
  return {
    company: form.company.trim(),
    model: form.model.trim(),
    type: form.type.trim(),
    price_per_day: Number(form.pricePerDay),
    seats: Number(form.seats),
    location: form.location.trim(),
    image_url: form.imageUrl.trim(),
    available: !!form.available,
    description: form.description.trim(),
    specifications: {
      engine: form.engine.trim(),
      fuel: form.fuel.trim(),
      transmission: form.transmission.trim(),
      luggageSpace: form.luggageSpace.trim(),
    },
    amenities: form.amenitiesText
      .split(",")
      .map((x) => x.trim())
      .filter(Boolean),
  };
}

export default function AdminCarsManager() {
  const { getToken } = useAuth();
  const [cars, setCars] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadCars = async () => {
    try {
      setLoading(true);
      setError("");
      const res = await axios.get(`${API}/cars`);
      setCars(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error(err);
      setError("Không tải được danh sách xe.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCars();
  }, []);

  const filteredCars = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    if (!q) return cars;

    return cars.filter((car) =>
      [car.company, car.model, car.type, car.location, car.description]
        .filter(Boolean)
        .join(" ")
        .toLowerCase()
        .includes(q)
    );
  }, [cars, keyword]);

  const updateField = (key, value) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleImageFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setError("Vui lòng chọn đúng file ảnh.");
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      updateField("imageUrl", reader.result);
      setError("");
    };
    reader.readAsDataURL(file);
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
    setMessage("");
    setError("");
  };

  const handleEdit = (car) => {
    setForm(normalizeCar(car));
    setEditingId(car.id);
    setMessage("");
    setError("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.company || !form.type || !form.pricePerDay || !form.seats) {
      setError("Nhập đủ hãng xe, loại xe, giá/ngày và số ghế.");
      return;
    }

    try {
      setSaving(true);
      setError("");
      setMessage("");

      const payload = buildPayload(form);
      const token = await getToken();

      if (editingId) {
        await axios.put(`${API}/cars/${editingId}`, payload, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMessage("Cập nhật xe thành công.");
      } else {
        await axios.post(`${API}/cars`, payload, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMessage("Thêm xe thành công.");
      }

      resetForm();
      await loadCars();
    } catch (err) {
      console.error(err);
      setError("Lưu xe thất bại.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    const ok = window.confirm("Bạn có chắc muốn xóa xe này?");
    if (!ok) return;

    try {
      setError("");
      setMessage("");
      const token = await getToken();
      await axios.delete(`${API}/cars/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessage("Xóa xe thành công.");
      if (editingId === id) resetForm();
      await loadCars();
    } catch (err) {
      console.error(err);
      setError("Xóa xe thất bại.");
    }
  };

  return (
    <div className="space-y-6">
      <div className="rounded-3xl bg-gradient-to-r from-sky-600 to-blue-600 p-6 text-white shadow-lg">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <h2 className="text-2xl font-bold">Quản lý Xe cho thuê</h2>
            <p className="mt-2 text-white/90">Tổng số: {cars.length} xe</p>
          </div>
          <button
            onClick={() => {
              resetForm();
              window.scrollTo({ top: 240, behavior: "smooth" });
            }}
            className="rounded-full bg-white px-6 py-3 font-semibold text-blue-600"
          >
            + Thêm xe
          </button>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.15fr_1fr]">
        <div className="rounded-3xl bg-white p-6 shadow-md">
          <h3 className="mb-6 text-3xl font-bold text-slate-800">
            {editingId ? "Cập nhật xe" : "Thêm xe mới"}
          </h3>

          {message && (
            <div className="mb-4 rounded-xl bg-green-50 px-4 py-3 text-green-700">
              {message}
            </div>
          )}

          {error && (
            <div className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-red-600">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="grid gap-5 md:grid-cols-2">
            <div>
              <label className="mb-2 block font-semibold text-slate-700">Hãng xe *</label>
              <input
                value={form.company}
                onChange={(e) => updateField("company", e.target.value)}
                placeholder="Toyota, Honda, ..."
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Loại xe *</label>
              <input
                value={form.type}
                onChange={(e) => updateField("type", e.target.value)}
                placeholder="Vios, Innova, ..."
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Model</label>
              <input
                value={form.model}
                onChange={(e) => updateField("model", e.target.value)}
                placeholder="2023 / AT / Premium"
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Giá/ngày (VNĐ) *</label>
              <input
                type="number"
                value={form.pricePerDay}
                onChange={(e) => updateField("pricePerDay", e.target.value)}
                placeholder="800000"
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Số ghế *</label>
              <select
                value={form.seats}
                onChange={(e) => updateField("seats", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              >
                <option value="4">4 chỗ</option>
                <option value="5">5 chỗ</option>
                <option value="7">7 chỗ</option>
                <option value="16">16 chỗ</option>
              </select>
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Trạng thái</label>
              <select
                value={form.available ? "available" : "unavailable"}
                onChange={(e) => updateField("available", e.target.value === "available")}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              >
                <option value="available">Có sẵn</option>
                <option value="unavailable">Đã thuê</option>
              </select>
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Địa điểm</label>
              <input
                value={form.location}
                onChange={(e) => updateField("location", e.target.value)}
                placeholder="TP.HCM"
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Chọn ảnh từ máy</label>
              <input
                type="file"
                accept="image/*"
                onChange={handleImageFileChange}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div className="md:col-span-2">
              <label className="mb-2 block font-semibold text-slate-700">Hoặc dán link ảnh</label>
              <input
                value={form.imageUrl}
                onChange={(e) => updateField("imageUrl", e.target.value)}
                placeholder="https://... hoặc sẽ tự điền khi chọn ảnh"
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            {form.imageUrl && (
              <div className="md:col-span-2">
                <label className="mb-2 block font-semibold text-slate-700">Xem trước ảnh</label>
                <img
                  src={form.imageUrl}
                  alt="preview"
                  className="h-44 w-full max-w-sm rounded-2xl border object-cover"
                />
              </div>
            )}

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Động cơ</label>
              <input
                value={form.engine}
                onChange={(e) => updateField("engine", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Nhiên liệu</label>
              <input
                value={form.fuel}
                onChange={(e) => updateField("fuel", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Hộp số</label>
              <input
                value={form.transmission}
                onChange={(e) => updateField("transmission", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div>
              <label className="mb-2 block font-semibold text-slate-700">Khoang hành lý</label>
              <input
                value={form.luggageSpace}
                onChange={(e) => updateField("luggageSpace", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div className="md:col-span-2">
              <label className="mb-2 block font-semibold text-slate-700">Mô tả</label>
              <textarea
                rows={3}
                value={form.description}
                onChange={(e) => updateField("description", e.target.value)}
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div className="md:col-span-2">
              <label className="mb-2 block font-semibold text-slate-700">
                Tiện nghi (cách nhau bởi dấu phẩy)
              </label>
              <input
                value={form.amenitiesText}
                onChange={(e) => updateField("amenitiesText", e.target.value)}
                placeholder="Máy lạnh, Camera lùi, Bluetooth"
                className="w-full rounded-2xl border px-4 py-3 outline-none"
              />
            </div>

            <div className="md:col-span-2 flex gap-3">
              <button
                type="submit"
                disabled={saving}
                className="rounded-2xl bg-blue-600 px-6 py-3 font-semibold text-white disabled:opacity-60"
              >
                {saving ? "Đang lưu..." : editingId ? "Cập nhật xe" : "Thêm xe"}
              </button>

              <button
                type="button"
                onClick={resetForm}
                className="rounded-2xl border px-6 py-3 font-semibold text-slate-700"
              >
                Reset
              </button>
            </div>
          </form>
        </div>

        <div className="rounded-3xl bg-white p-6 shadow-md">
          <div className="mb-4 flex items-center justify-between gap-4">
            <h3 className="text-2xl font-bold text-slate-800">Danh sách xe</h3>
            <input
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="Tìm hãng, loại, địa điểm..."
              className="w-full max-w-xs rounded-2xl border px-4 py-3 outline-none"
            />
          </div>

          {loading ? (
            <p className="text-slate-500">Đang tải dữ liệu...</p>
          ) : filteredCars.length === 0 ? (
            <p className="text-slate-500">Chưa có xe nào.</p>
          ) : (
            <div className="space-y-4">
              {filteredCars.map((car) => (
                <div
                  key={car.id}
                  className="overflow-hidden rounded-2xl border border-slate-200 shadow-sm"
                >
                  {(car.image_url || car.imageUrl) ? (
                    <img
                      src={car.image_url || car.imageUrl}
                      alt={`${car.company} ${car.model}`}
                      className="h-44 w-full object-cover"
                    />
                  ) : (
                    <div className="flex h-44 w-full items-center justify-center bg-slate-100 text-slate-400">
                      Chưa có ảnh
                    </div>
                  )}

                  <div className="p-4">
                    <div className="flex items-start justify-between gap-4">
                      <div>
                        <h4 className="text-lg font-bold text-slate-800">
                          {car.company} {car.model}
                        </h4>
                        <p className="mt-1 text-slate-600">{car.type}</p>
                        <p className="mt-1 text-sm text-slate-500">
                          {car.location || "Chưa có địa điểm"} · {car.seats} chỗ
                        </p>
                        <p className="mt-2 font-semibold text-blue-600">
                          {((car.price_per_day ?? car.pricePerDay) || 0).toLocaleString("vi-VN")} đ/ngày
                        </p>
                      </div>

                      <span
                        className={`rounded-full px-3 py-1 text-xs font-semibold ${
                          car.available
                            ? "bg-green-100 text-green-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {car.available ? "Có sẵn" : "Đã thuê"}
                      </span>
                    </div>

                    <div className="mt-4 flex gap-2">
                      <button
                        onClick={() => handleEdit(car)}
                        className="rounded-xl bg-amber-500 px-4 py-2 text-white"
                      >
                        Sửa
                      </button>
                      <button
                        onClick={() => handleDelete(car.id)}
                        className="rounded-xl bg-red-600 px-4 py-2 text-white"
                      >
                        Xóa
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}