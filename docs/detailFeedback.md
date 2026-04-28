# Chi tiết phản ánh - Full Source Code

## App.tsx

```tsx
import {
  ArrowLeft,
  RefreshCw,
  MapPin,
  Calendar,
  FileText,
  Image as ImageIcon,
  Building2,
} from "lucide-react";

export default function App() {
  const feedbackData = {
    status: "Chờ xử lý",
    code: "#PH00800219-008",
    type: "Rò rỉ nước",
    location: "dsadsadd",
    date: "28/04/2026",
    description: "sadsdasasd",
    images: [
      "https://images.unsplash.com/photo-1563207153-f403bf289096?w=400&h=300&fit=crop",
      "https://images.unsplash.com/photo-1581092918484-8313de5e6f06?w=400&h=300&fit=crop",
      "https://images.unsplash.com/photo-1581092918484-8313de5e6f06?w=400&h=300&fit=crop",
    ],
    companyFeedback: null,
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Header */}
      <div className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
              <ArrowLeft className="w-5 h-5 text-gray-700" />
            </button>
            <h1 className="text-xl font-semibold text-gray-900">
              Chi tiết phản ánh
            </h1>
          </div>
          <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
            <RefreshCw className="w-5 h-5 text-gray-600" />
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-4xl mx-auto px-4 py-6 space-y-4">
        {/* Status Badge */}
        <div className="bg-white rounded-2xl shadow-sm p-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-3 h-3 bg-amber-500 rounded-full animate-pulse"></div>
              <span className="text-amber-700 font-semibold bg-amber-50 px-4 py-2 rounded-full">
                {feedbackData.status}
              </span>
            </div>
            <span className="text-sm text-gray-500 font-mono">
              {feedbackData.code}
            </span>
          </div>

          {/* Info Grid */}
          <div className="space-y-4">
            <div className="flex items-start gap-3 p-4 bg-blue-50 rounded-xl">
              <FileText className="w-5 h-5 text-blue-600 mt-0.5 flex-shrink-0" />
              <div className="flex-1">
                <p className="text-sm text-gray-600 mb-1">Loại vấn đề</p>
                <p className="font-semibold text-gray-900">
                  {feedbackData.type}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 p-4 bg-green-50 rounded-xl">
              <MapPin className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
              <div className="flex-1">
                <p className="text-sm text-gray-600 mb-1">Vị trí</p>
                <p className="font-semibold text-gray-900">
                  {feedbackData.location}
                </p>
              </div>
            </div>

            <div className="flex items-start gap-3 p-4 bg-purple-50 rounded-xl">
              <Calendar className="w-5 h-5 text-purple-600 mt-0.5 flex-shrink-0" />
              <div className="flex-1">
                <p className="text-sm text-gray-600 mb-1">Ngày gửi</p>
                <p className="font-semibold text-gray-900">
                  {feedbackData.date}
                </p>
              </div>
            </div>

            <div className="p-4 bg-gray-50 rounded-xl">
              <p className="text-sm text-gray-600 mb-2">Mô tả</p>
              <p className="text-gray-900 leading-relaxed">
                {feedbackData.description}
              </p>
            </div>
          </div>
        </div>

        {/* Images Section */}
        <div className="bg-white rounded-2xl shadow-sm p-6">
          <div className="flex items-center gap-2 mb-4">
            <ImageIcon className="w-5 h-5 text-gray-700" />
            <h2 className="font-semibold text-gray-900">Hình ảnh đính kèm</h2>
            <span className="text-sm text-gray-500 ml-auto">
              {feedbackData.images.length} ảnh
            </span>
          </div>
          <div className="grid grid-cols-3 gap-3">
            {feedbackData.images.map((img, idx) => (
              <div
                key={idx}
                className="aspect-square rounded-xl overflow-hidden bg-gray-100 hover:scale-105 transition-transform cursor-pointer shadow-sm hover:shadow-md"
              >
                <img
                  src={img}
                  alt={`Hình ${idx + 1}`}
                  className="w-full h-full object-cover"
                />
              </div>
            ))}
          </div>
        </div>

        {/* Company Feedback Section */}
        <div className="bg-white rounded-2xl shadow-sm p-6">
          <div className="flex items-center gap-2 mb-4">
            <Building2 className="w-5 h-5 text-gray-700" />
            <h2 className="font-semibold text-gray-900">Phản hồi từ công ty</h2>
          </div>
          {feedbackData.companyFeedback ? (
            <div className="p-4 bg-blue-50 rounded-xl">
              <p className="text-gray-900">{feedbackData.companyFeedback}</p>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-8">
              <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-3">
                <Building2 className="w-8 h-8 text-gray-400" />
              </div>
              <p className="text-gray-500 text-center">Chưa có phản hồi</p>
              <p className="text-sm text-gray-400 text-center mt-1">
                Công ty sẽ phản hồi sớm nhất có thể
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
```

## Thông tin dự án

- **Framework**: React 18.3.1
- **Styling**: Tailwind CSS v4
- **Icons**: Lucide React
- **Build Tool**: Vite

## Cài đặt

```bash
pnpm install
```

## Chạy development

```bash
pnpm run dev
```
