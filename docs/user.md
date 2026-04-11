sau khi đăng nhập se co toktn bỏ vài header se lây được dữ liệu
màn hình thông tin khách hàng
get : http://125.253.121.171/api/v1/qlkh/customers/me

"data": {
"digiCode": "00800219",
"name": "NGUYỄN HỮU ĐỒNG",
"address": "Khu TĐC 15HA, Phường Phú Mỹ, TP Hồ Chí Minh",
"phone": "0973123477",
"email": "",
"sms": "0932087346",
"taxCode": "",
"isActive": 0,
"isWaterCut": 0
},
"message": "Lấy thông tin khách hàng thành công",
"statusCode": 200

layout của reactjs hãy chuyển đổi code qua phù họp với dự an android
import { motion } from "motion/react";
import { useState } from "react";
import {
User,
MapPin,
Phone,
Mail,
Hash,
Edit,
MessageSquare,
Shield,
Droplets,
CheckCircle2,
XCircle,
Settings,
ChevronRight,
Bell,
Lock,
HelpCircle
} from "lucide-react";

const userData = {
digiCode: "00800219",
name: "NGUYỄN HỮU ĐỒNG",
address: "Khu TĐC 15HA, Phường Phú Mỹ, TP Hồ Chí Minh",
phone: "0973123477",
email: "",
sms: "0932087346",
taxCode: "",
isActive: 0,
isWaterCut: 0
};

export default function App() {
const [isEditing, setIsEditing] = useState(false);

return (
<div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100">
{/_ Header _/}
<div className="bg-gradient-to-br from-blue-500 via-blue-600 to-blue-700 px-4 pt-12 pb-24">
<motion.div
initial={{ opacity: 0, y: -20 }}
animate={{ opacity: 1, y: 0 }}
className="max-w-md mx-auto" >
<div className="flex items-center justify-between mb-8">
<h1 className="text-white text-2xl">Thông tin cá nhân</h1>
<motion.button
whileTap={{ scale: 0.9 }}
className="p-2 bg-white/20 rounded-full text-white" >
<Settings className="w-5 h-5" />
</motion.button>
</div>

          {/* Profile Card */}
          <div className="bg-white/10 backdrop-blur-md rounded-2xl p-6">
            <div className="flex items-center gap-4 mb-4">
              <div className="w-20 h-20 bg-white rounded-full flex items-center justify-center">
                <User className="w-10 h-10 text-blue-500" />
              </div>
              <div className="flex-1">
                <h2 className="text-white text-xl mb-1">{userData.name}</h2>
                <p className="text-white/80 text-sm">Mã KH: {userData.digiCode}</p>
              </div>
            </div>

            {/* Status Badges */}
            <div className="flex gap-3">
              <div className={`flex items-center gap-2 px-3 py-1.5 rounded-full ${
                userData.isActive === 1 ? "bg-green-500/20" : "bg-red-500/20"
              }`}>
                {userData.isActive === 1 ? (
                  <CheckCircle2 className="w-4 h-4 text-green-300" />
                ) : (
                  <XCircle className="w-4 h-4 text-red-300" />
                )}
                <span className={userData.isActive === 1 ? "text-green-100 text-sm" : "text-red-100 text-sm"}>
                  {userData.isActive === 1 ? "Đang hoạt động" : "Không hoạt động"}
                </span>
              </div>

              <div className={`flex items-center gap-2 px-3 py-1.5 rounded-full ${
                userData.isWaterCut === 0 ? "bg-blue-500/20" : "bg-orange-500/20"
              }`}>
                <Droplets className={`w-4 h-4 ${userData.isWaterCut === 0 ? "text-blue-300" : "text-orange-300"}`} />
                <span className={`text-sm ${userData.isWaterCut === 0 ? "text-blue-100" : "text-orange-100"}`}>
                  {userData.isWaterCut === 0 ? "Đang cung cấp" : "Đã cắt nước"}
                </span>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {/* Main Content */}
      <div className="max-w-md mx-auto px-4 -mt-12 pb-8">
        {/* Contact Information */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-gray-900 text-lg">Thông tin liên hệ</h3>
            <motion.button
              whileTap={{ scale: 0.9 }}
              onClick={() => setIsEditing(!isEditing)}
              className="p-2 bg-blue-50 rounded-full text-blue-500 hover:bg-blue-100 transition-colors"
            >
              <Edit className="w-4 h-4" />
            </motion.button>
          </div>

          <div className="space-y-4">
            {/* Phone */}
            <div className="flex items-start gap-4 pb-4 border-b border-gray-100">
              <div className="p-2.5 bg-blue-50 rounded-xl">
                <Phone className="w-5 h-5 text-blue-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Số điện thoại</p>
                <p className="text-gray-900">{userData.phone}</p>
              </div>
            </div>

            {/* SMS */}
            <div className="flex items-start gap-4 pb-4 border-b border-gray-100">
              <div className="p-2.5 bg-green-50 rounded-xl">
                <MessageSquare className="w-5 h-5 text-green-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Số điện thoại SMS</p>
                <p className="text-gray-900">{userData.sms}</p>
              </div>
            </div>

            {/* Email */}
            <div className="flex items-start gap-4 pb-4 border-b border-gray-100">
              <div className="p-2.5 bg-purple-50 rounded-xl">
                <Mail className="w-5 h-5 text-purple-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Email</p>
                <p className="text-gray-900">
                  {userData.email || <span className="text-gray-400 italic">Chưa cập nhật</span>}
                </p>
              </div>
            </div>

            {/* Address */}
            <div className="flex items-start gap-4">
              <div className="p-2.5 bg-red-50 rounded-xl">
                <MapPin className="w-5 h-5 text-red-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Địa chỉ</p>
                <p className="text-gray-900 leading-relaxed">{userData.address}</p>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Additional Info */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <h3 className="text-gray-900 text-lg mb-4">Thông tin bổ sung</h3>

          <div className="space-y-4">
            {/* Customer Code */}
            <div className="flex items-start gap-4 pb-4 border-b border-gray-100">
              <div className="p-2.5 bg-indigo-50 rounded-xl">
                <Hash className="w-5 h-5 text-indigo-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Mã khách hàng</p>
                <p className="text-gray-900 font-mono">{userData.digiCode}</p>
              </div>
            </div>

            {/* Tax Code */}
            <div className="flex items-start gap-4">
              <div className="p-2.5 bg-orange-50 rounded-xl">
                <Shield className="w-5 h-5 text-orange-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm mb-1">Mã số thuế</p>
                <p className="text-gray-900">
                  {userData.taxCode || <span className="text-gray-400 italic">Chưa cập nhật</span>}
                </p>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Account Status */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <h3 className="text-gray-900 text-lg mb-4">Trạng thái tài khoản</h3>

          <div className="grid grid-cols-2 gap-4">
            <div className={`p-4 rounded-xl border-2 ${
              userData.isActive === 1
                ? "bg-green-50 border-green-200"
                : "bg-red-50 border-red-200"
            }`}>
              <div className={`w-10 h-10 rounded-full flex items-center justify-center mb-3 ${
                userData.isActive === 1 ? "bg-green-500" : "bg-red-500"
              }`}>
                {userData.isActive === 1 ? (
                  <CheckCircle2 className="w-5 h-5 text-white" />
                ) : (
                  <XCircle className="w-5 h-5 text-white" />
                )}
              </div>
              <p className="text-gray-600 text-sm mb-1">Tài khoản</p>
              <p className={`font-medium ${userData.isActive === 1 ? "text-green-700" : "text-red-700"}`}>
                {userData.isActive === 1 ? "Hoạt động" : "Không hoạt động"}
              </p>
            </div>

            <div className={`p-4 rounded-xl border-2 ${
              userData.isWaterCut === 0
                ? "bg-blue-50 border-blue-200"
                : "bg-orange-50 border-orange-200"
            }`}>
              <div className={`w-10 h-10 rounded-full flex items-center justify-center mb-3 ${
                userData.isWaterCut === 0 ? "bg-blue-500" : "bg-orange-500"
              }`}>
                <Droplets className="w-5 h-5 text-white" />
              </div>
              <p className="text-gray-600 text-sm mb-1">Cung cấp nước</p>
              <p className={`font-medium ${userData.isWaterCut === 0 ? "text-blue-700" : "text-orange-700"}`}>
                {userData.isWaterCut === 0 ? "Bình thường" : "Đã cắt"}
              </p>
            </div>
          </div>
        </motion.div>

        {/* Settings Menu */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="bg-white rounded-2xl shadow-lg overflow-hidden mb-4"
        >
          <button className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-50 rounded-xl">
                <Bell className="w-5 h-5 text-blue-500" />
              </div>
              <span className="text-gray-900">Thông báo</span>
            </div>
            <ChevronRight className="w-5 h-5 text-gray-400" />
          </button>

          <div className="border-t border-gray-100" />

          <button className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-green-50 rounded-xl">
                <Lock className="w-5 h-5 text-green-500" />
              </div>
              <span className="text-gray-900">Bảo mật</span>
            </div>
            <ChevronRight className="w-5 h-5 text-gray-400" />
          </button>

          <div className="border-t border-gray-100" />

          <button className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-50 rounded-xl">
                <HelpCircle className="w-5 h-5 text-purple-500" />
              </div>
              <span className="text-gray-900">Trợ giúp</span>
            </div>
            <ChevronRight className="w-5 h-5 text-gray-400" />
          </button>
        </motion.div>

        {/* Update Button */}
        <motion.button
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
          whileTap={{ scale: 0.98 }}
          className="w-full py-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-2xl shadow-lg hover:shadow-xl transition-shadow"
        >
          Cập nhật thông tin
        </motion.button>
      </div>
    </div>

);
}
