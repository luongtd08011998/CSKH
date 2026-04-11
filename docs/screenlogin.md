Hãy chuyen code nay sang android để trở thành màn hình đăng nhập mới

import { motion } from "motion/react";
import { useState } from "react";
import {
Droplets,
Hash,
Phone,
ArrowRight,
Eye,
EyeOff,
ShieldCheck
} from "lucide-react";

export default function App() {
const [customerCode, setCustomerCode] = useState("");
const [phoneNumber, setPhoneNumber] = useState("");
const [showPassword, setShowPassword] = useState(false);
const [isLoading, setIsLoading] = useState(false);

const handleLogin = () => {
setIsLoading(true);
// Simulate login
setTimeout(() => {
setIsLoading(false);
}, 2000);
};

return (
<div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50 flex items-center justify-center p-4">
<div className="w-full max-w-md">
{/_ Logo & Branding _/}
<motion.div
initial={{ opacity: 0, y: -30 }}
animate={{ opacity: 1, y: 0 }}
transition={{ duration: 0.8 }}
className="text-center mb-12" >
{/_ Logo _/}
<motion.div
initial={{ scale: 0 }}
animate={{ scale: 1 }}
transition={{ type: "spring", duration: 1, delay: 0.2 }}
className="relative w-24 h-24 mx-auto mb-6" >
<div className="absolute inset-0 bg-gradient-to-br from-blue-400 to-blue-600 rounded-3xl rotate-6 opacity-20" />
<div className="absolute inset-0 bg-gradient-to-br from-blue-500 to-blue-700 rounded-3xl flex items-center justify-center shadow-2xl">
<Droplets className="w-12 h-12 text-white" strokeWidth={2.5} />
</div>
<motion.div
className="absolute -top-1 -right-1 w-6 h-6 bg-blue-400 rounded-full"
animate={{
                scale: [1, 1.2, 1],
                opacity: [1, 0.7, 1]
              }}
transition={{ duration: 2, repeat: Infinity }}
/>
</motion.div>

          {/* Company Name */}
          <motion.h1
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.4 }}
            className="text-3xl text-gray-900 mb-2"
          >
            AquaPure
          </motion.h1>

          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.5 }}
            className="text-gray-500"
          >
            Hệ thống quản lý khách hàng
          </motion.p>
        </motion.div>

        {/* Login Card */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.3 }}
          className="bg-white rounded-3xl shadow-2xl p-8"
        >
          <div className="mb-8">
            <h2 className="text-2xl text-gray-900 mb-2">Đăng nhập</h2>
            <p className="text-gray-500">Vui lòng nhập thông tin để tiếp tục</p>
          </div>

          {/* Customer Code Input */}
          <div className="mb-6">
            <label className="block text-gray-700 mb-3">Mã khách hàng</label>
            <div className="relative">
              <div className="absolute left-4 top-1/2 -translate-y-1/2">
                <Hash className="w-5 h-5 text-gray-400" />
              </div>
              <motion.input
                whileFocus={{ scale: 1.01 }}
                type="text"
                value={customerCode}
                onChange={(e) => setCustomerCode(e.target.value)}
                placeholder="Nhập mã khách hàng"
                className="w-full pl-12 pr-4 py-4 bg-gray-50 border-2 border-gray-200 rounded-2xl focus:border-blue-500 focus:bg-white focus:outline-none transition-all text-gray-900 placeholder:text-gray-400"
              />
            </div>
          </div>

          {/* Phone Number Input */}
          <div className="mb-6">
            <label className="block text-gray-700 mb-3">Số điện thoại</label>
            <div className="relative">
              <div className="absolute left-4 top-1/2 -translate-y-1/2">
                <Phone className="w-5 h-5 text-gray-400" />
              </div>
              <motion.input
                whileFocus={{ scale: 1.01 }}
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="Nhập số điện thoại"
                className="w-full pl-12 pr-14 py-4 bg-gray-50 border-2 border-gray-200 rounded-2xl focus:border-blue-500 focus:bg-white focus:outline-none transition-all text-gray-900 placeholder:text-gray-400"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showPassword ? (
                  <EyeOff className="w-5 h-5" />
                ) : (
                  <Eye className="w-5 h-5" />
                )}
              </button>
            </div>
          </div>

          {/* Remember & Forgot */}
          <div className="flex items-center justify-between mb-8">
            <label className="flex items-center gap-2 cursor-pointer group">
              <div className="relative">
                <input
                  type="checkbox"
                  className="peer w-5 h-5 appearance-none border-2 border-gray-300 rounded checked:bg-blue-500 checked:border-blue-500 transition-all cursor-pointer"
                />
                <motion.div
                  className="absolute inset-0 flex items-center justify-center pointer-events-none"
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                >
                  <svg
                    className="w-3 h-3 text-white opacity-0 peer-checked:opacity-100"
                    viewBox="0 0 12 12"
                    fill="none"
                  >
                    <path
                      d="M2 6L5 9L10 3"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                </motion.div>
              </div>
              <span className="text-gray-600 text-sm group-hover:text-gray-900 transition-colors">
                Ghi nhớ đăng nhập
              </span>
            </label>

            <button className="text-blue-500 text-sm hover:text-blue-600 transition-colors">
              Quên mật khẩu?
            </button>
          </div>

          {/* Login Button */}
          <motion.button
            whileTap={{ scale: 0.98 }}
            onClick={handleLogin}
            disabled={isLoading || !customerCode || !phoneNumber}
            className="w-full py-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-2xl shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2 group"
          >
            {isLoading ? (
              <div className="flex items-center gap-3">
                <motion.div
                  animate={{ rotate: 360 }}
                  transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                  className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                />
                <span>Đang đăng nhập...</span>
              </div>
            ) : (
              <>
                <span>Đăng nhập</span>
                <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
              </>
            )}
          </motion.button>



        {/* Background Decorations */}
        <div className="fixed top-20 right-10 w-64 h-64 bg-blue-200 rounded-full blur-3xl opacity-20 -z-10" />
        <div className="fixed bottom-20 left-10 w-80 h-80 bg-blue-300 rounded-full blur-3xl opacity-20 -z-10" />
      </div>
    </div>

);
}
