# Plan: Triển khai tính năng Lịch sử Phản ánh

## Context

Tính năng gửi phản ánh đã hoàn chỉnh (form + multipart upload + success state). Tuy nhiên, **không có UI hiển thị lịch sử phản ánh** — `GetFeedbacksUseCase` và `FeedbackRepository.getFeedbacks()` đã implement nhưng chưa được dùng. Theo tài liệu BE, API `GET /api/v1/qlkh/customer/feedbacks` trả về danh sách với status (PENDING/PROCESSING/RESOLVED/REJECTED), cần hiển thị cho user theo dõi.

## Approach: Thêm tab "Lịch sử" vào PhanAnhScreen

Thay vì tạo screen riêng, thêm 2 tab vào PhanAnhScreen: **"Gửi phản ánh"** | **"Lịch sử"** — UX liền mạch, user gửi xong chuyển tab để xem lịch sử.

## Files cần tạo

### 1. `composeApp/src/commonMain/kotlin/com/example/cskh/presentation/screens/phananh/FeedbackListViewModel.kt`

- State: `FeedbackListUiState(items, isLoading, errorMessage)`
- Inject `GetFeedbacksUseCase`
- `refresh()` gọi use case, load danh sách
- Init tự load

### 2. `composeApp/src/commonMain/kotlin/com/example/cskh/presentation/screens/phananh/FeedbackHistoryTab.kt`

- Composable hiển thị danh sách phản ánh
- LazyColumn với `FeedbackItemCard` cho từng item
- Mỗi card hiển thị: trackingCode, issueType label (map từ key sang tiếng Việt), location, description (2 dòng), status badge (màu theo trạng thái), createdAt
- Status màu: PENDING→Vàng, PROCESSING→Xanh dương, RESOLVED→Xanh lá, REJECTED→Đỏ
- Pull-to-refresh, empty state, error state, loading state
- Nhấn vào card → expand chi tiết (ảnh, mô tả đầy đủ)

## Files cần sửa

### 3. `composeApp/src/commonMain/kotlin/com/example/cskh/presentation/screens/phananh/PhanAnhScreen.kt`

- Thêm tab bar ở top: "Gửi phản ánh" | "Lịch sử"
- Tab "Gửi phản ánh": giữ nguyên FormState hiện tại
- Tab "Lịch sử": gọi `FeedbackHistoryTab`
- Inject `FeedbackListViewModel` riêng (scope ViewModel theo tab)

### 4. `composeApp/src/commonMain/kotlin/com/example/cskh/di/AppModule.kt`

- Thêm `single { GetFeedbacksUseCase(get(), get()) }` (hiện bị thiếu!)
- Thêm `viewModel { FeedbackListViewModel(get()) }`

## Map issueType → tiếng Việt (dùng chung)

```kotlin
leak → "Rò rỉ nước"
quality → "Chất lượng nước"
pressure → "Áp lực nước yếu"
outage → "Mất nước"
billing → "Hóa đơn"
meter → "Đồng hồ nước"
other → "Khác"
```

## Map status → màu sắc

```kotlin
PENDING → Vàng (#F59E0B bg, #92400E text)
PROCESSING → Xanh dương (#3B82F6 bg, #1E3A8A text)
RESOLVED → Xanh lá (#10B981 bg, #064E3B text)
REJECTED → Đỏ (#EF4444 bg, #7F1D1D text)
```

## Kiến trúc

```
PhanAnhScreen (tab host)
├── Tab "Gửi phản ánh" → FormState (existing)
└── Tab "Lịch sử" → FeedbackHistoryTab
                      └── FeedbackListViewModel → GetFeedbacksUseCase → FeedbackRepository.getFeedbacks()
```

## Verification

1. Build project: `./gradlew composeApp:assembleDebug`
2. Mở app → Home → Phản ánh → thấy 2 tab
3. Tab "Gửi phản ánh" vẫn hoạt động như cũ
4. Tab "Lịch sử" hiển thị danh sách phản ánh với status badge
5. Kéo để refresh hoạt động
6. Empty state hiển thị đúng khi chưa có phản ánh
