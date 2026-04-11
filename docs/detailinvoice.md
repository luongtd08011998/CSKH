màn hình chi tiết hóa đơn viết bằng reactjs hãy chuyển code sang android cho tôi giư nguyên cấu trúc

<div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-500 to-blue-600 px-4 pt-12 pb-8">
        <div className="max-w-md mx-auto">
          <div className="flex items-center gap-4 mb-6">
            <motion.button
              whileTap={{ scale: 0.9 }}
              className="p-2 bg-white/20 rounded-full text-white"
            >
              <ArrowLeft className="w-5 h-5" />
            </motion.button>
            <h1 className="text-white text-xl flex-1">Chi tiết hóa đơn</h1>
            <motion.button
              whileTap={{ scale: 0.9 }}
              className="p-2 bg-white/20 rounded-full text-white"
            >
              <Share2 className="w-5 h-5" />
            </motion.button>
          </div>

          {/* Invoice Status Card */}
          <div className="bg-white/10 backdrop-blur-md rounded-2xl p-5">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <FileText className="w-5 h-5 text-white" />
                <span className="text-white text-sm">Mã hóa đơn</span>
              </div>
              <span className="text-white font-mono">{invoiceData.invoiceNumber}</span>
            </div>

            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Calendar className="w-5 h-5 text-white" />
                <span className="text-white text-sm">Kỳ hóa đơn</span>
              </div>
              <span className="text-white">{invoiceData.period}</span>
            </div>

            <div className="flex items-center justify-between pt-3 border-t border-white/20">
              <span className="text-white/80 text-sm">Trạng thái</span>
              <div className={`flex items-center gap-2 px-3 py-1 rounded-full ${
                invoiceData.paymentStatus === 0
                  ? "bg-green-500/20"
                  : invoiceData.paymentStatus === 1
                  ? "bg-orange-500/20"
                  : "bg-red-500/20"
              }`}>
                {invoiceData.paymentStatus === 0 ? (
                  <CheckCircle2 className="w-4 h-4 text-green-300" />
                ) : invoiceData.paymentStatus === 1 ? (
                  <AlertCircle className="w-4 h-4 text-orange-300" />
                ) : (
                  <XCircle className="w-4 h-4 text-red-300" />
                )}
                <span className={`text-sm ${
                  invoiceData.paymentStatus === 0
                    ? "text-green-100"
                    : invoiceData.paymentStatus === 1
                    ? "text-orange-100"
                    : "text-red-100"
                }`}>
                  {invoiceData.paymentStatusLabel}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-md mx-auto px-4 -mt-4 pb-8">
        {/* Customer Info */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <h3 className="text-gray-900 text-lg mb-4">Thông tin khách hàng</h3>

          <div className="space-y-3">
            <div className="flex items-center gap-3 pb-3 border-b border-gray-100">
              <div className="p-2 bg-blue-50 rounded-lg">
                <User className="w-5 h-5 text-blue-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm">Tên khách hàng</p>
                <p className="text-gray-900">{invoiceData.customerName}</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-50 rounded-lg">
                <Hash className="w-5 h-5 text-purple-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-500 text-sm">Mã khách hàng</p>
                <p className="text-gray-900 font-mono">{invoiceData.digiCode}</p>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Water Usage */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <h3 className="text-gray-900 text-lg mb-4">Lượng nước tiêu thụ</h3>

          <div className="grid grid-cols-3 gap-4 mb-4">
            <div className="text-center p-3 bg-blue-50 rounded-xl">
              <Droplets className="w-6 h-6 text-blue-500 mx-auto mb-2" />
              <p className="text-2xl text-gray-900 mb-1">{invoiceData.oldVal}</p>
              <p className="text-gray-500 text-xs">Chỉ số cũ (m³)</p>
            </div>

            <div className="text-center p-3 bg-green-50 rounded-xl">
              <TrendingUp className="w-6 h-6 text-green-500 mx-auto mb-2" />
              <p className="text-2xl text-gray-900 mb-1">{invoiceData.newVal}</p>
              <p className="text-gray-500 text-xs">Chỉ số mới (m³)</p>
            </div>

            <div className="text-center p-3 bg-orange-50 rounded-xl">
              <Receipt className="w-6 h-6 text-orange-500 mx-auto mb-2" />
              <p className="text-2xl text-orange-600 mb-1">{waterUsed}</p>
              <p className="text-gray-500 text-xs">Tiêu thụ (m³)</p>
            </div>
          </div>

          <div className="bg-gradient-to-r from-blue-50 to-green-50 rounded-xl p-4">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center shadow-sm">
                <Droplets className="w-6 h-6 text-blue-500" />
              </div>
              <div className="flex-1">
                <p className="text-gray-900">Lượng nước sử dụng</p>
                <p className="text-blue-600 text-2xl">{waterUsed} m³</p>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Payment Details */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-4"
        >
          <h3 className="text-gray-900 text-lg mb-4">Chi tiết thanh toán</h3>

          <div className="space-y-3">
            {/* Water Amount */}
            <div className="flex items-center justify-between py-2">
              <div className="flex items-center gap-2">
                <Droplets className="w-4 h-4 text-blue-500" />
                <span className="text-gray-600">Tiền nước</span>
              </div>
              <span className="text-gray-900">
                {invoiceData.amount.toLocaleString('vi-VN')}đ
              </span>
            </div>

            {/* Environment Fee */}
            <div className="flex items-center justify-between py-2">
              <div className="flex items-center gap-2">
                <Leaf className="w-4 h-4 text-green-500" />
                <span className="text-gray-600">Phí môi trường</span>
              </div>
              <span className="text-gray-900">
                {invoiceData.envFee.toLocaleString('vi-VN')}đ
              </span>
            </div>

            {/* Tax */}
            <div className="flex items-center justify-between py-2 pb-3 border-b border-gray-200">
              <div className="flex items-center gap-2">
                <Receipt className="w-4 h-4 text-purple-500" />
                <span className="text-gray-600">Thuế GTGT (5%)</span>
              </div>
              <span className="text-gray-900">
                {invoiceData.taxFee.toLocaleString('vi-VN')}đ
              </span>
            </div>

            {/* Total */}
            <div className="flex items-center justify-between py-3 bg-gradient-to-r from-blue-50 to-blue-100 rounded-xl px-4">
              <div className="flex items-center gap-2">
                <DollarSign className="w-5 h-5 text-blue-600" />
                <span className="text-gray-900">Tổng cộng</span>
              </div>
              <span className="text-2xl text-blue-600">
                {invoiceData.totalAmount.toLocaleString('vi-VN')}đ
              </span>
            </div>
          </div>
        </motion.div>

        {/* Payment Dates */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-2xl p-5 shadow-lg mb-6"
        >
          <h3 className="text-gray-900 text-lg mb-4">Thông tin thanh toán</h3>

          <div className="grid grid-cols-2 gap-4">
            <div className="p-3 bg-gray-50 rounded-xl">
              <p className="text-gray-500 text-sm mb-1">Ngày phát hành</p>
              <p className="text-gray-900">{invoiceData.invoiceDate}</p>
            </div>

            <div className="p-3 bg-orange-50 rounded-xl">
              <p className="text-gray-500 text-sm mb-1">Hạn thanh toán</p>
              <p className="text-orange-600">{invoiceData.dueDate}</p>
            </div>
          </div>
        </motion.div>

        {/* Action Buttons */}
        {invoiceData.paymentStatus === 1 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="grid grid-cols-2 gap-4 mb-4"
          >
            <motion.button
              whileTap={{ scale: 0.98 }}
              className="flex items-center justify-center gap-2 px-6 py-4 bg-white border-2 border-blue-500 text-blue-500 rounded-2xl shadow-lg hover:bg-blue-50 transition-colors"
            >
              <Download className="w-5 h-5" />
              <span>Tải về</span>
            </motion.button>

            <motion.button
              whileTap={{ scale: 0.98 }}
              onClick={() => setShowPayment(true)}
              className="flex items-center justify-center gap-2 px-6 py-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-2xl shadow-lg hover:shadow-xl transition-shadow"
            >
              <CreditCard className="w-5 h-5" />
              <span>Thanh toán</span>
            </motion.button>
          </motion.div>
        )}

        {invoiceData.paymentStatus === 0 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="bg-green-50 border-2 border-green-200 rounded-2xl p-4 mb-4"
          >
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-green-500 rounded-full flex items-center justify-center">
                <CheckCircle2 className="w-6 h-6 text-white" />
              </div>
              <div className="flex-1">
                <p className="text-green-900">Đã thanh toán</p>
                <p className="text-green-600 text-sm">Cảm ơn bạn đã thanh toán đúng hạn</p>
              </div>
            </div>
          </motion.div>
        )}

        {/* Payment Warning for Unpaid */}
        {invoiceData.paymentStatus === 1 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="bg-orange-50 border-2 border-orange-200 rounded-2xl p-4"
          >
            <div className="flex items-start gap-3">
              <AlertCircle className="w-5 h-5 text-orange-500 mt-0.5" />
              <div>
                <p className="text-orange-900 mb-1">Lưu ý thanh toán</p>
                <p className="text-orange-700 text-sm">
                  Vui lòng thanh toán trước ngày {invoiceData.dueDate} để tránh bị cắt nước
                </p>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </div>

);
