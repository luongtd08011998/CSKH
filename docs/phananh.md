import { useState } from 'react';
import { Camera, MapPin, Clock, AlertCircle, Send, CheckCircle } from 'lucide-react';

export default function App() {
const [formData, setFormData] = useState({
issueType: '',
address: '',
description: '',
photos: [] as File[]
});
const [submitted, setSubmitted] = useState(false);

const issueTypes = [
{ value: 'leak', label: 'Rò rỉ nước', icon: '💧' },
{ value: 'quality', label: 'Chất lượng nước', icon: '🚰' },
{ value: 'pressure', label: 'Áp lực nước yếu', icon: '📉' },
{ value: 'outage', label: 'Mất nước', icon: '🚫' },
{ value: 'billing', label: 'Hóa đơn', icon: '💵' },
{ value: 'meter', label: 'Đồng hồ nước', icon: '⏱️' },
{ value: 'other', label: 'Khác', icon: '📝' }
];

const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
if (e.target.files) {
setFormData({ ...formData, photos: Array.from(e.target.files) });
}
};

const handleSubmit = (e: React.FormEvent) => {
e.preventDefault();
setSubmitted(true);
setTimeout(() => setSubmitted(false), 3000);
};

if (submitted) {
return (
<div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 flex items-center justify-center p-4">
<div className="bg-white rounded-3xl shadow-2xl p-8 max-w-md w-full text-center">
<div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
<CheckCircle className="w-12 h-12 text-green-600" />
</div>
<h2 className="text-2xl font-bold text-gray-900 mb-3">Gửi thành công!</h2>
<p className="text-gray-600 mb-2">Phản ánh của bạn đã được tiếp nhận</p>
<p className="text-sm text-gray-500">Mã số: #PH{Math.floor(Math.random() \* 100000)}</p>
<p className="text-sm text-gray-500 mt-4">Chúng tôi sẽ liên hệ trong vòng 24h</p>
<button
onClick={() => setSubmitted(false)}
className="mt-6 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors" >
Gửi phản ánh mới
</button>
</div>
</div>
);
}

return (
<div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100 p-4 pb-8">
<div className="max-w-2xl mx-auto">
{/_ Header _/}
<div className="bg-white rounded-3xl shadow-lg p-6 mb-6">
<h1 className="text-2xl font-bold text-gray-900 mb-2">Phản ánh dịch vụ</h1>
<p className="text-gray-600">Gửi thông tin để chúng tôi hỗ trợ bạn</p>
</div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Issue Type */}
          <div className="bg-white rounded-3xl shadow-lg p-6">
            <label className="block text-sm font-semibold text-gray-700 mb-4">
              Loại vấn đề <span className="text-red-500">*</span>
            </label>
            <div className="grid grid-cols-2 gap-3">
              {issueTypes.map((type) => (
                <button
                  key={type.value}
                  type="button"
                  onClick={() => setFormData({ ...formData, issueType: type.value })}
                  className={`p-4 rounded-2xl border-2 transition-all ${
                    formData.issueType === type.value
                      ? 'border-blue-600 bg-blue-50 shadow-md'
                      : 'border-gray-200 hover:border-blue-300'
                  }`}
                >
                  <div className="text-3xl mb-2">{type.icon}</div>
                  <div className="text-sm font-medium text-gray-900">{type.label}</div>
                </button>
              ))}
            </div>
          </div>

          {/* Address */}
          <div className="bg-white rounded-3xl shadow-lg p-6">
            <label className="block text-sm font-semibold text-gray-700 mb-3">
              Địa điểm <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="text"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                placeholder="Nhập địa chỉ cụ thể"
                className="w-full pl-12 pr-4 py-4 border-2 border-gray-200 rounded-2xl focus:border-blue-600 focus:outline-none transition-colors"
              />
            </div>
            <p className="text-xs text-gray-500 mt-2 ml-1">VD: Số nhà, tên đường, phường/xã</p>
          </div>

          {/* Description */}
          <div className="bg-white rounded-3xl shadow-lg p-6">
            <label className="block text-sm font-semibold text-gray-700 mb-3">
              Mô tả chi tiết <span className="text-red-500">*</span>
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Mô tả tình trạng, thời gian xảy ra, ảnh hưởng..."
              rows={5}
              className="w-full px-4 py-4 border-2 border-gray-200 rounded-2xl focus:border-blue-600 focus:outline-none resize-none transition-colors"
            />
            <div className="flex items-center gap-2 mt-2 text-xs text-gray-500">
              <Clock className="w-4 h-4" />
              <span>Thời gian phát hiện: {new Date().toLocaleString('vi-VN')}</span>
            </div>
          </div>

          {/* Photos */}
          <div className="bg-white rounded-3xl shadow-lg p-6">
            <label className="block text-sm font-semibold text-gray-700 mb-3">
              Hình ảnh minh họa
              <span className="text-gray-500 font-normal ml-2">(Tùy chọn)</span>
            </label>
            <label className="flex flex-col items-center justify-center w-full h-40 border-2 border-dashed border-gray-300 rounded-2xl cursor-pointer hover:border-blue-400 transition-colors bg-gray-50">
              <Camera className="w-10 h-10 text-gray-400 mb-2" />
              <span className="text-sm text-gray-600">Chụp hoặc chọn ảnh</span>
              <span className="text-xs text-gray-500 mt-1">Tối đa 5 ảnh</span>
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={handlePhotoUpload}
                className="hidden"
              />
            </label>
            {formData.photos.length > 0 && (
              <div className="mt-3 flex gap-2 flex-wrap">
                {formData.photos.map((photo, idx) => (
                  <div key={idx} className="px-3 py-2 bg-blue-50 rounded-lg text-sm text-blue-700">
                    📷 {photo.name}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Info Note */}
          <div className="bg-blue-50 border-2 border-blue-200 rounded-2xl p-4 flex gap-3">
            <AlertCircle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
            <div className="text-sm text-blue-800">
              <p className="font-medium mb-1">Lưu ý:</p>
              <ul className="space-y-1 text-blue-700">
                <li>• Phản ánh khẩn cấp sẽ được xử lý trong vòng 4h</li>
                <li>• Bạn sẽ nhận mã số theo dõi sau khi gửi</li>
                <li>• Thông tin liên hệ lấy từ hồ sơ đã đăng ký</li>
              </ul>
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={!formData.issueType || !formData.address || !formData.description}
            className="w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white py-5 rounded-2xl font-semibold text-lg shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-3"
          >
            <Send className="w-5 h-5" />
            Gửi phản ánh
          </button>
        </form>
      </div>
    </div>

);
}
