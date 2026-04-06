import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Professional Icon Components
const IconUsers = () => (
  <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
  </svg>
);

const IconCar = () => (
  <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12" />
  </svg>
);

const IconCarLarge = () => (
  <svg className="w-24 h-24" fill="none" viewBox="0 0 24 24" strokeWidth={1} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.129-.504 1.09-1.124a17.902 17.902 0 00-3.213-9.193 2.056 2.056 0 00-1.58-.86H14.25M16.5 18.75h-2.25m0-11.177v-.958c0-.568-.422-1.048-.987-1.106a48.554 48.554 0 00-10.026 0 1.106 1.106 0 00-.987 1.106v7.635m12-6.677v6.677m0 4.5v-4.5m0 0h-12" />
  </svg>
);

const IconShield = () => (
  <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
  </svg>
);

const IconStar = () => (
  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
    <path d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
  </svg>
);

const IconCheck = () => (
  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
  </svg>
);

const IconPhone = () => (
  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 6.75c0 8.284 6.716 15 15 15h2.25a2.25 2.25 0 002.25-2.25v-1.372c0-.516-.351-.966-.852-1.091l-4.423-1.106c-.44-.11-.902.055-1.173.417l-.97 1.293c-.282.376-.769.542-1.21.38a12.035 12.035 0 01-7.143-7.143c-.162-.441.004-.928.38-1.21l1.293-.97c.363-.271.527-.734.417-1.173L6.963 3.102a1.125 1.125 0 00-1.091-.852H4.5A2.25 2.25 0 002.25 4.5v2.25z" />
  </svg>
);

const IconMail = () => (
  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
  </svg>
);

const IconLocation = () => (
  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
    <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
  </svg>
);

const normalizeCar = (car) => ({
  ...car,
  pricePerDay: Number(car?.pricePerDay ?? car?.price_per_day ?? 0),
  imageUrl: car?.imageUrl ?? car?.image_url ?? '',
  specifications:
    typeof car?.specifications === 'string'
      ? (() => {
          try {
            let parsed = JSON.parse(car.specifications);
            if (typeof parsed === 'string') parsed = JSON.parse(parsed);
            return parsed;
          } catch {
            return {};
          }
        })()
      : car?.specifications || {},
  amenities:
    typeof car?.amenities === 'string'
      ? (() => {
          try {
            let parsed = JSON.parse(car.amenities);
            if (typeof parsed === 'string') parsed = JSON.parse(parsed);
            return Array.isArray(parsed) ? parsed : [];
          } catch {
            return [];
          }
        })()
      : Array.isArray(car?.amenities)
        ? car.amenities
        : [],
});

export default function CarsPage() {
  const navigate = useNavigate();
  const [rows, setRows] = useState([]);
  const [selectedCar, setSelectedCar] = useState(null);
  const [pickupDate, setPickupDate] = useState('');
  const [returnDate, setReturnDate] = useState('');

  const sampleCars = [
    {
      id: 1,
      company: 'Toyota',
      type: 'Vios',
      seats: 5,
      price_per_day: 800000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe sedan 5 chỗ tiết kiệm nhiên liệu, phù hợp cho gia đình và du lịch ngắn ngày',
      specifications: {
        engine: '1.5L',
        fuel: 'Xăng',
        transmission: 'Số tự động',
        luggageSpace: '470L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Camera lùi']
    },
    {
      id: 2,
      company: 'Honda',
      type: 'City',
      seats: 5,
      price_per_day: 850000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe sedan hiện đại với công nghệ tiên tiến, tiết kiệm nhiên liệu và an toàn',
      specifications: {
        engine: '1.5L',
        fuel: 'Xăng',
        transmission: 'Số tự động',
        luggageSpace: '500L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Camera lùi', 'Wifi']
    },
    {
      id: 3,
      company: 'Hyundai',
      type: 'Grand i10',
      seats: 5,
      price_per_day: 600000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe hatchback nhỏ gọn, dễ lái, phù hợp cho thành phố',
      specifications: {
        engine: '1.2L',
        fuel: 'Xăng',
        transmission: 'Số sàn',
        luggageSpace: '300L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB']
    },
    {
      id: 4,
      company: 'Ford',
      type: 'Everest',
      seats: 7,
      price_per_day: 1500000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'SUV 7 chỗ mạnh mẽ, phù hợp cho gia đình lớn và đường địa hình',
      specifications: {
        engine: '2.0L Turbo',
        fuel: 'Diesel',
        transmission: 'Số tự động',
        luggageSpace: '1050L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Wifi', 'TV', '4WD', 'Nội thất da']
    },
    {
      id: 5,
      company: 'Toyota',
      type: 'Innova',
      seats: 7,
      price_per_day: 1200000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'MPV 7 chỗ rộng rãi, thoải mái cho gia đình và nhóm bạn',
      specifications: {
        engine: '2.0L',
        fuel: 'Xăng',
        transmission: 'Số tự động',
        luggageSpace: '300L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Camera lùi', 'TV']
    },
    {
      id: 6,
      company: 'Mercedes',
      type: 'C-Class',
      seats: 5,
      price_per_day: 3000000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe sang trọng với nội thất cao cấp và công nghệ hiện đại',
      specifications: {
        engine: '2.0L Turbo',
        fuel: 'Xăng',
        transmission: 'Số tự động',
        luggageSpace: '480L'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Wifi', 'TV', 'Nội thất da', 'Cửa sổ trời']
    },
    {
      id: 7,
      company: 'Toyota',
      type: 'Hiace',
      seats: 16,
      price_per_day: 2000000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe khách 16 chỗ rộng rãi, phù hợp cho tour du lịch và công tác',
      specifications: {
        engine: '2.8L',
        fuel: 'Diesel',
        transmission: 'Số sàn',
        luggageSpace: 'Lớn'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'TV', 'Ghế ngả']
    },
    {
      id: 8,
      company: 'Ford',
      type: 'Transit',
      seats: 16,
      price_per_day: 1800000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe khách 16 chỗ hiện đại, tiện nghi cho nhóm du lịch',
      specifications: {
        engine: '2.2L',
        fuel: 'Diesel',
        transmission: 'Số sàn',
        luggageSpace: 'Lớn'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'TV', 'Ghế ngả']
    },
    {
      id: 9,
      company: 'Isuzu',
      type: 'Samco',
      seats: 50,
      price_per_day: 5000000,
      available: true,
      image_url: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
      description: 'Xe khách 50 chỗ lớn, phù hợp cho tour đoàn và sự kiện',
      specifications: {
        engine: '6.0L',
        fuel: 'Diesel',
        transmission: 'Số sàn',
        luggageSpace: 'Rất lớn'
      },
      amenities: ['Điều hòa', 'Bluetooth', 'USB', 'Wifi', 'TV', 'Ghế ngả', 'Tủ lạnh']
    }
  ];

  const load = async () => {
    try {
      const res = await axios.get(`${API}/cars`);
      const normalized = Array.isArray(res.data)
        ? res.data.map(normalizeCar)
        : [];
      setRows(normalized);
    } catch (error) {
      console.error('Error loading cars:', error);
      setRows(sampleCars.map(normalizeCar));
    }
  };

  useEffect(() => {
    load();
  }, []);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN').format(price || 0);
  };

  const handleBookCar = (car) => {
    if (!pickupDate || !returnDate) {
      alert('Vui lòng chọn ngày lấy xe và ngày trả xe!');
      return;
    }

    const pickup = new Date(pickupDate);
    const returnD = new Date(returnDate);

    if (returnD <= pickup) {
      alert('Ngày trả xe phải sau ngày lấy xe!');
      return;
    }

    const rentalDays = Math.ceil((returnD - pickup) / (1000 * 60 * 60 * 24));

    const cartItem = {
      id: `car-${car.id}-${Date.now()}`,
      name: `Thuê xe ${car.company} ${car.type}`,
      type: `${car.seats} chỗ`,
      price: parseFloat(car.pricePerDay || 0),
      quantity: rentalDays,
      image: car.imageUrl,
      details: {
        car_id: car.id,
        company: car.company,
        model: car.type,
        seats: car.seats,
        specifications: car.specifications,
        amenities: car.amenities,
        pickup_date: pickupDate,
        return_date: returnDate,
        rental_days: rentalDays
      }
    };

    try {
      const existingCart = JSON.parse(localStorage.getItem('pendingCart') || '[]');
      const updatedCart = [...existingCart, cartItem];
      localStorage.setItem('pendingCart', JSON.stringify(updatedCart));
      navigate('/checkout');
    } catch (e) {
      console.error('Failed to save to cart', e);
      alert('Có lỗi xảy ra. Vui lòng thử lại!');
    }
  };

  const calculateRentalDays = () => {
    if (!pickupDate || !returnDate) return 0;
    const pickup = new Date(pickupDate);
    const returnD = new Date(returnDate);
    if (returnD <= pickup) return 0;
    return Math.ceil((returnD - pickup) / (1000 * 60 * 60 * 24));
  };

  const handleCloseModal = () => {
    setSelectedCar(null);
    setPickupDate('');
    setReturnDate('');
  };

  const cars = rows.length > 0 ? rows : sampleCars.map(normalizeCar);

  const statistics = [
    { number: '10,000+', label: 'Khách hàng hài lòng', icon: <IconUsers /> },
    { number: '500+', label: 'Xe đa dạng', icon: <IconCar /> },
    { number: '99%', label: 'Tỷ lệ hài lòng', icon: <IconStar /> },
    { number: '24/7', label: 'Hỗ trợ thuê xe', icon: <IconShield /> }
  ];

  const values = [
    {
      title: 'Xe mới, chất lượng',
      description: 'Tất cả xe đều được bảo dưỡng định kỳ, đảm bảo an toàn và vận hành tốt',
      icon: <IconCar />
    },
    {
      title: 'Giá cả hợp lý',
      description: 'Giá thuê minh bạch, không phát sinh chi phí ẩn, nhiều chương trình khuyến mãi',
      icon: <IconCheck />
    },
    {
      title: 'Dịch vụ chuyên nghiệp',
      description: 'Đội ngũ nhân viên tận tâm, hỗ trợ 24/7 và giao xe tận nơi',
      icon: <IconUsers />
    },
    {
      title: 'Bảo hiểm đầy đủ',
      description: 'Tất cả xe đều có bảo hiểm đầy đủ, đảm bảo an toàn cho khách hàng',
      icon: <IconShield />
    }
  ];

  const carTypes = [
    { name: 'Xe 5 chỗ', icon: '🚗', count: cars.filter(c => Number(c.seats) === 5).length },
    { name: 'Xe 7 chỗ', icon: '🚙', count: cars.filter(c => Number(c.seats) === 7).length },
    { name: 'Xe 16 chỗ', icon: '🚐', count: cars.filter(c => Number(c.seats) === 16).length },
    { name: 'Xe 50 chỗ', icon: '🚌', count: cars.filter(c => Number(c.seats) === 50).length }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-white via-blue-50/30 to-white">
      <section className="relative bg-gradient-to-br from-blue-900 via-blue-800 to-sky-700 text-white py-20 md:py-28 overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div
            className="absolute top-0 left-0 w-full h-full"
            style={{
              backgroundImage: `url("data:image/svg+xml,%3Csvg width='100' height='100' xmlns='http://www.w3.org/2000/svg'%3E%3Cdefs%3E%3Cpattern id='grid' width='100' height='100' patternUnits='userSpaceOnUse'%3E%3Cpath d='M 100 0 L 0 0 0 100' fill='none' stroke='%23ffffff' stroke-width='1'/%3E%3C/pattern%3E%3C/defs%3E%3Crect width='100' height='100' fill='url(%23grid)'/%3E%3C/svg%3E")`,
              backgroundSize: '50px 50px'
            }}
          ></div>
        </div>

        <div className="absolute top-20 right-10 w-72 h-72 bg-blue-500/20 rounded-full blur-3xl"></div>
        <div className="absolute bottom-20 left-10 w-96 h-96 bg-sky-500/20 rounded-full blur-3xl"></div>

        <div className="max-w-7xl mx-auto px-4 relative z-10">
          <div className="text-center max-w-4xl mx-auto">
            <div className="inline-flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm border border-white/20 rounded-full mb-8 shadow-lg">
              <IconShield />
              <span className="text-sm font-medium">Cho thuê xe chuyên nghiệp</span>
            </div>

            <h1 className="text-5xl md:text-6xl lg:text-7xl font-extrabold mb-6 leading-tight">
              Cho Thuê <span className="text-transparent bg-clip-text bg-gradient-to-r from-sky-300 to-cyan-200">Xe</span>
            </h1>
            <p className="text-xl md:text-2xl text-blue-100/90 mb-10 leading-relaxed max-w-3xl mx-auto">
              Đa dạng loại xe từ xe 5 chỗ cho gia đình đến xe khách 16 chỗ và xe 50 chỗ.
              Jurni mang đến dịch vụ cho thuê xe chất lượng cao với giá cả hợp lý.
            </p>
            <div className="flex flex-wrap justify-center gap-4">
              <a href="#xe" className="group relative bg-white text-blue-700 px-8 py-4 rounded-full font-bold hover:scale-105 transition-all duration-300 shadow-2xl hover:shadow-blue-500/50 overflow-hidden">
                <span className="relative z-10 flex items-center gap-2">
                  Xem danh sách xe
                  <svg className="w-5 h-5 transform group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
                  </svg>
                </span>
                <div className="absolute inset-0 bg-gradient-to-r from-blue-50 to-sky-50 opacity-0 group-hover:opacity-100 transition-opacity rounded-full"></div>
              </a>
              <a href="#lien-he" className="group bg-white/10 backdrop-blur-md text-white px-8 py-4 rounded-full font-bold hover:bg-white/20 transition-all duration-300 border-2 border-white/30 hover:border-white/50 shadow-xl">
                <span className="flex items-center gap-2">
                  <IconPhone />
                  Liên hệ ngay
                </span>
              </a>
            </div>
          </div>
        </div>
      </section>

      <section className="py-16 bg-gradient-to-b from-white to-gray-50">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-14">
            <h2 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4">
              Thành Công Được <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-sky-600">Chứng Minh</span>
            </h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">Những con số nói lên chất lượng dịch vụ của chúng tôi</p>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {statistics.map((stat, idx) => (
              <div key={idx} className="group relative text-center p-8 bg-white rounded-3xl border-2 border-gray-100 hover:border-orange-500 hover:shadow-2xl transition-all duration-300 overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-50/0 to-sky-50/0 group-hover:from-blue-50 group-hover:to-sky-50 transition-all duration-300"></div>

                <div className="relative z-10">
                  <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-blue-600 to-sky-600 text-white rounded-2xl mb-6 shadow-lg group-hover:scale-110 group-hover:rotate-3 transition-transform duration-300">
                    {stat.icon}
                  </div>
                  <div className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-3 group-hover:text-orange-600 transition-colors">
                    {stat.number}
                  </div>
                  <div className="text-sm font-semibold text-gray-600 uppercase tracking-wide">{stat.label}</div>
                </div>

                <div className="absolute top-0 right-0 w-20 h-20 bg-gradient-to-br from-blue-500/10 to-transparent rounded-bl-full opacity-0 group-hover:opacity-100 transition-opacity"></div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-20 bg-white relative overflow-hidden">
        <div
          className="absolute inset-0 opacity-5"
          style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23000000'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`
          }}
        ></div>

        <div className="max-w-7xl mx-auto px-4 relative z-10">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4">
              Giá Trị & <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-sky-600">Dịch Vụ</span> Của Jurni
            </h2>
            <p className="text-lg text-gray-600 max-w-3xl mx-auto leading-relaxed">
              Chúng tôi không chỉ cho thuê xe, mà còn mang đến dịch vụ chất lượng với những giá trị cốt lõi
            </p>
          </div>
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, idx) => (
              <div key={idx} className="group relative bg-white p-8 rounded-3xl border-2 border-gray-100 hover:border-orange-500 hover:shadow-2xl transition-all duration-300 overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-50/0 to-sky-50/0 group-hover:from-blue-50 group-hover:to-sky-50 transition-all duration-300"></div>

                <div className="relative z-10">
                  <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-blue-600 to-sky-600 text-white rounded-2xl mb-6 shadow-lg group-hover:scale-110 group-hover:-rotate-6 transition-transform duration-300">
                    {value.icon}
                  </div>
                  <h3 className="text-xl font-bold text-gray-900 mb-3 group-hover:text-orange-600 transition-colors">
                    {value.title}
                  </h3>
                  <p className="text-gray-600 text-sm leading-relaxed">{value.description}</p>
                </div>

                <div className="absolute top-0 right-0 w-0 h-0 border-t-[40px] border-r-[40px] border-t-blue-500/10 border-r-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-20 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-7xl mx-auto px-4">
          <div className="relative bg-gradient-to-br from-blue-900 via-blue-800 to-sky-700 rounded-[2.5rem] p-10 md:p-16 text-white overflow-hidden shadow-2xl">
            <div className="absolute inset-0 opacity-10">
              <div className="absolute top-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl"></div>
              <div className="absolute bottom-0 left-0 w-96 h-96 bg-sky-400 rounded-full blur-3xl"></div>
            </div>

            <div className="absolute top-0 left-0 w-32 h-32 border-t-[3px] border-l-[3px] border-white/20 rounded-tl-[2.5rem]"></div>
            <div className="absolute bottom-0 right-0 w-32 h-32 border-b-[3px] border-r-[3px] border-white/20 rounded-br-[2.5rem]"></div>

            <div className="grid md:grid-cols-2 gap-12 relative z-10">
              <div>
                <div className="inline-flex items-center gap-2 px-4 py-2 bg-white/10 backdrop-blur-sm border border-white/20 rounded-full mb-6">
                  <IconShield />
                  <span className="text-sm font-semibold">Cam kết chất lượng</span>
                </div>
                <h2 className="text-4xl md:text-5xl font-extrabold mb-6 leading-tight">
                  Chất Lượng - <span className="text-transparent bg-clip-text bg-gradient-to-r from-sky-300 to-cyan-200">Mục Tiêu Hàng Đầu</span>
                </h2>
                <p className="text-lg text-blue-100/90 mb-8 leading-relaxed">
                  Tại Jurni, mỗi chiếc xe đều được đảm bảo:
                </p>
                <ul className="space-y-4">
                  {[
                    'Xe mới, được bảo dưỡng định kỳ',
                    'Bảo hiểm đầy đủ, an toàn tuyệt đối',
                    'Dịch vụ hỗ trợ 24/7, luôn sẵn sàng',
                    'Giao xe tận nơi, thuận tiện',
                    'Giá cả minh bạch, không phát sinh chi phí ẩn'
                  ].map((item, idx) => (
                    <li key={idx} className="flex items-start gap-4 group">
                      <div className="flex-shrink-0 w-8 h-8 bg-green-400/20 rounded-xl flex items-center justify-center group-hover:bg-green-400/30 transition-colors">
                        <IconCheck />
                      </div>
                      <span className="text-blue-100/90 text-base leading-relaxed pt-1">{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="relative">
                <div className="bg-white/10 backdrop-blur-md rounded-3xl p-10 border-2 border-white/20 shadow-2xl">
                  <div className="text-center">
                    <div className="text-7xl font-extrabold mb-4 bg-gradient-to-br from-white to-blue-200 text-transparent bg-clip-text">
                      100%
                    </div>
                    <div className="text-2xl font-bold text-white mb-6">Xe đạt chuẩn</div>
                    <div className="text-base text-blue-100/90 leading-relaxed max-w-sm mx-auto">
                      Tất cả xe đều được kiểm tra và bảo dưỡng trước khi cho thuê
                    </div>
                  </div>

                  <div className="absolute -top-4 -right-4 w-24 h-24 bg-white/10 rounded-2xl rotate-12"></div>
                  <div className="absolute -bottom-4 -left-4 w-20 h-20 bg-sky-400/20 rounded-2xl -rotate-12"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-20 bg-gradient-to-b from-white via-gray-50 to-white">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4">
              Đa Dạng <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-sky-600">Loại Xe</span>
            </h2>
            <p className="text-lg text-gray-600 max-w-3xl mx-auto leading-relaxed">
              Từ xe 5 chỗ cho gia đình đến xe khách 16 chỗ và xe 50 chỗ, chúng tôi có đủ loại xe phù hợp với mọi nhu cầu
            </p>
          </div>
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {carTypes.map((type, idx) => (
              <div key={idx} className="group relative bg-white rounded-3xl p-8 shadow-lg border-2 border-gray-100 hover:border-orange-500 hover:shadow-2xl transition-all duration-300 overflow-hidden text-center">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-50/0 to-sky-50/0 group-hover:from-blue-50 group-hover:to-sky-50 transition-all duration-300"></div>

                <div className="relative z-10">
                  <div className="text-6xl mb-6 transform group-hover:scale-110 transition-transform duration-300">{type.icon}</div>
                  <h3 className="text-xl font-bold text-gray-900 mb-2 group-hover:text-orange-600 transition-colors">{type.name}</h3>
                  <div className="text-blue-600 font-semibold">{type.count} xe</div>
                </div>

                <div className="absolute top-0 right-0 w-0 h-0 border-t-[50px] border-r-[50px] border-t-blue-500/10 border-r-transparent opacity-0 group-hover:opacity-100 transition-opacity"></div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section id="xe" className="py-20 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4">
              Danh Sách <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-sky-600">Xe</span>
            </h2>
            <p className="text-lg text-gray-600 max-w-3xl mx-auto">
              Khám phá các loại xe và đặt thuê ngay hôm nay
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {cars.map((car) => (
              <div
                key={car.id}
                className="group bg-white rounded-3xl overflow-hidden shadow-lg border-2 border-gray-100 hover:border-orange-500 hover:shadow-2xl transition-all duration-300"
              >
                <div className="relative overflow-hidden">
                  {car.imageUrl ? (
                    <img
                      src={car.imageUrl}
                      alt={`${car.company} ${car.type}`}
                      className="w-full h-56 object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                  ) : (
                    <div className="w-full h-56 bg-gradient-to-br from-blue-400 to-sky-500 flex items-center justify-center">
                      <IconCarLarge />
                    </div>
                  )}
                  {!car.available && (
                    <div className="absolute top-4 right-4 bg-red-600 text-white px-3 py-1 rounded-full text-xs font-semibold">
                      Đã thuê
                    </div>
                  )}
                </div>

                <div className="p-6">
                  <h3 className="text-xl font-bold text-gray-900 mb-1">{car.company} {car.type}</h3>
                  <div className="flex items-center gap-4 text-sm text-gray-600 mb-3">
                    <span className="flex items-center gap-1">
                      <IconUsers />
                      {car.seats} chỗ
                    </span>
                  </div>

                  <div className="flex items-center justify-between pt-4 border-t border-gray-200">
                    <div>
                      <div className="text-2xl font-bold" style={{ color: '#FF6B35' }}>
                        {formatPrice(car.pricePerDay)} VND
                      </div>
                      <div className="text-xs text-gray-500">/ ngày</div>
                    </div>
                    <button
                      onClick={() => setSelectedCar(car)}
                      className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-full font-semibold transition"
                    >
                      Chi tiết
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {selectedCar && (
            <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4" onClick={handleCloseModal}>
              <div className="bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                <div className="sticky top-0 bg-white border-b border-gray-200 p-6 flex justify-between items-center z-10">
                  <h2 className="text-2xl font-bold text-gray-900">{selectedCar.company} {selectedCar.type}</h2>
                  <button
                    onClick={handleCloseModal}
                    className="text-gray-400 hover:text-gray-600 text-2xl"
                  >
                    ×
                  </button>
                </div>

                <div className="p-6 space-y-6">
                  {selectedCar.imageUrl && (
                    <img
                      src={selectedCar.imageUrl}
                      alt={`${selectedCar.company} ${selectedCar.type}`}
                      className="w-full h-64 object-cover rounded-xl"
                    />
                  )}

                  <div>
                    <div className="mb-4">
                      <div className="text-3xl font-bold mb-2" style={{ color: '#FF6B35' }}>
                        {formatPrice(selectedCar.pricePerDay)} VND
                      </div>
                      <div className="text-gray-600">/ ngày</div>
                    </div>

                    <div className="space-y-3 mb-4">
                      <div className="flex items-center gap-2 text-gray-700">
                        <IconUsers />
                        <span className="font-medium">Số chỗ: {selectedCar.seats} chỗ</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${selectedCar.available ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                          {selectedCar.available ? 'Có sẵn' : 'Đã thuê'}
                        </span>
                      </div>
                    </div>

                    {selectedCar.description && (
                      <p className="text-gray-700 mb-4">{selectedCar.description}</p>
                    )}
                  </div>

                  <div className="mb-6 bg-gradient-to-br from-blue-50 to-sky-50 rounded-xl p-6 border-2 border-blue-200">
                    <h3 className="text-xl font-bold text-gray-900 mb-4">🚗 Thời gian thuê xe</h3>
                    <div className="grid md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Ngày lấy xe *
                        </label>
                        <input
                          type="date"
                          value={pickupDate}
                          onChange={(e) => setPickupDate(e.target.value)}
                          min={new Date().toISOString().split('T')[0]}
                          className="w-full border-2 border-blue-300 rounded-lg px-4 py-3 focus:outline-none focus:border-blue-500"
                          required
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Ngày trả xe *
                        </label>
                        <input
                          type="date"
                          value={returnDate}
                          onChange={(e) => setReturnDate(e.target.value)}
                          min={pickupDate || new Date().toISOString().split('T')[0]}
                          className="w-full border-2 border-blue-300 rounded-lg px-4 py-3 focus:outline-none focus:border-blue-500"
                          required
                        />
                      </div>
                    </div>

                    {calculateRentalDays() > 0 && (
                      <div className="mt-4 pt-4 border-t border-blue-300">
                        <div className="flex justify-between items-center">
                          <div>
                            <span className="text-sm text-gray-600">Số ngày thuê:</span>
                            <span className="ml-2 text-lg font-bold text-blue-600">{calculateRentalDays()} ngày</span>
                          </div>
                          <div className="text-right">
                            <div className="text-sm text-gray-600 mb-1">Tổng tiền:</div>
                            <div className="text-3xl font-extrabold" style={{ color: '#FF6B35' }}>
                              {formatPrice(selectedCar.pricePerDay * calculateRentalDays())} đ
                            </div>
                            <div className="text-xs text-gray-500">
                              {formatPrice(selectedCar.pricePerDay)} đ/ngày × {calculateRentalDays()} ngày
                            </div>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>

                  {selectedCar.specifications && (
                    <div className="mb-6">
                      <h3 className="text-xl font-bold text-gray-900 mb-4">Thông số kỹ thuật</h3>
                      <div className="grid md:grid-cols-2 gap-4">
                        {(() => {
                          const specs = selectedCar.specifications;

                          if (!specs || typeof specs !== 'object' || Array.isArray(specs)) {
                            return <p className="text-gray-500 text-sm">Không có thông tin kỹ thuật</p>;
                          }

                          const labelMap = {
                            engine: 'Động cơ',
                            fuel: 'Nhiên liệu',
                            transmission: 'Hộp số',
                            luggageSpace: 'Khoang hành lý'
                          };

                          const entries = Object.entries(specs);
                          if (entries.length === 0) {
                            return <p className="text-gray-500 text-sm">Chưa có thông tin kỹ thuật</p>;
                          }

                          return entries.map(([key, value]) => (
                            <div key={key} className="bg-blue-50 rounded-lg p-4">
                              <div className="text-sm text-gray-600 mb-1">{labelMap[key] || key}</div>
                              <div className="font-semibold text-gray-900">{value || 'N/A'}</div>
                            </div>
                          ));
                        })()}
                      </div>
                    </div>
                  )}

                  {selectedCar.amenities && (
                    <div className="mb-6">
                      <h3 className="text-xl font-bold text-gray-900 mb-4">Tiện nghi</h3>
                      <div className="grid md:grid-cols-2 gap-3">
                        {Array.isArray(selectedCar.amenities) && selectedCar.amenities.length > 0 ? (
                          selectedCar.amenities.map((amenity, idx) => (
                            <div key={idx} className="flex items-center gap-3 bg-blue-50 rounded-lg p-3">
                              <IconCheck />
                              <span className="text-gray-700">{amenity}</span>
                            </div>
                          ))
                        ) : (
                          <p className="text-gray-500 text-sm col-span-2">Không có thông tin tiện nghi</p>
                        )}
                      </div>
                    </div>
                  )}

                  <div className="mb-6">
                    <h3 className="text-xl font-bold text-gray-900 mb-4">Điều kiện thuê xe</h3>
                    <div className="space-y-4">
                      <div className="border-l-4 border-blue-500 pl-4">
                        <div className="font-semibold text-gray-900 mb-1">Yêu cầu</div>
                        <div className="text-sm text-gray-600">
                          Bằng lái xe hợp lệ, CMND/CCCD, đặt cọc 30% giá trị hợp đồng
                        </div>
                      </div>
                      <div className="border-l-4 border-green-500 pl-4">
                        <div className="font-semibold text-gray-900 mb-1">Hủy đặt xe</div>
                        <div className="text-sm text-gray-600">
                          Miễn phí hủy trước 48 giờ. Hủy trong vòng 48 giờ: phí 20% giá trị đơn hàng.
                        </div>
                      </div>
                      <div className="border-l-4 border-yellow-500 pl-4">
                        <div className="font-semibold text-gray-900 mb-1">Đổi ngày</div>
                        <div className="text-sm text-gray-600">
                          Có thể đổi ngày thuê, vui lòng liên hệ trước 24 giờ.
                        </div>
                      </div>
                      <div className="border-l-4 border-purple-500 pl-4">
                        <div className="font-semibold text-gray-900 mb-1">Bảo hiểm</div>
                        <div className="text-sm text-gray-600">
                          Xe đã có bảo hiểm đầy đủ. Khách hàng chịu trách nhiệm trong trường hợp vi phạm giao thông.
                        </div>
                      </div>
                    </div>
                  </div>

                  <div id="lien-he" className="bg-gradient-to-r from-blue-600 to-sky-600 rounded-xl p-6 text-white">
                    <h3 className="text-xl font-bold mb-4">Liên Hệ Thuê Xe</h3>
                    <div className="grid md:grid-cols-2 gap-4">
                      <a href="tel:1900123456" className="flex items-center gap-3 bg-white/20 backdrop-blur-sm rounded-lg p-4 hover:bg-white/30 transition">
                        <IconPhone />
                        <div>
                          <div className="text-sm text-blue-100">Hotline</div>
                          <div className="font-semibold">1900 123 456</div>
                        </div>
                      </a>
                      <a href="mailto:cars@jurni.com" className="flex items-center gap-3 bg-white/20 backdrop-blur-sm rounded-lg p-4 hover:bg-white/30 transition">
                        <IconMail />
                        <div>
                          <div className="text-sm text-blue-100">Email</div>
                          <div className="font-semibold">cars@jurni.com</div>
                        </div>
                      </a>
                      <div className="flex items-center gap-3 bg-white/20 backdrop-blur-sm rounded-lg p-4">
                        <IconLocation />
                        <div>
                          <div className="text-sm text-blue-100">Địa chỉ</div>
                          <div className="font-semibold">123 Đường ABC, Quận XYZ, TP.HCM</div>
                        </div>
                      </div>
                      <div className="flex items-center gap-3 bg-white/20 backdrop-blur-sm rounded-lg p-4">
                        <IconShield />
                        <div>
                          <div className="text-sm text-blue-100">Hỗ trợ</div>
                          <div className="font-semibold">24/7 - Tất cả các ngày</div>
                        </div>
                      </div>
                    </div>
                    <button
                      className="mt-4 w-full bg-white px-6 py-3 rounded-full font-semibold transition"
                      style={{ color: '#FF6B35' }}
                      onClick={() => handleBookCar(selectedCar)}
                      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#FFE8E0')}
                      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#FFFFFF')}
                    >
                      Đặt thuê xe ngay
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}