# Hướng dẫn CSS cho Android

## 1. Màu sắc (Colors)

### Background màu pastel cho icon

```css
bg-blue-100    → #DBEAFE (RGB: 219, 234, 254)
bg-green-100   → #D1FAE5 (RGB: 209, 250, 229)
bg-teal-100    → #CCFBF1 (RGB: 204, 251, 241)
bg-red-100     → #FEE2E2 (RGB: 254, 226, 226)
bg-purple-100  → #F3E8FF (RGB: 243, 232, 255)
```

### Màu icon

```css
text-blue-600   → #2563EB (RGB: 37, 99, 235)
text-green-600  → #059669 (RGB: 5, 150, 105)
text-teal-600   → #0D9488 (RGB: 13, 148, 136)
text-red-600    → #DC2626 (RGB: 220, 38, 38)
text-purple-600 → #9333EA (RGB: 147, 51, 234)
text-blue-500   → #3B82F6 (RGB: 59, 130, 246)
```

### Màu khác

```css
bg-gray-50     → #F9FAFB (RGB: 249, 250, 251) - Background tổng
bg-white       → #FFFFFF (RGB: 255, 255, 255) - Background card
text-gray-700  → #374151 (RGB: 55, 65, 81)   - Text màu
text-gray-800  → #1F2937 (RGB: 31, 41, 55)   - Header màu
```

## 2. Kích thước (Sizes)

### Icon container

```css
w-16 h-16 → width: 64px, height: 64px
```

### Icon

```css
w-7 h-7 → width: 28px, height: 28px
```

### Gap (khoảng cách giữa các items)

```css
gap-4 → 16px
gap-2 → 8px
```

### Padding

```css
p-6 → padding: 24px (all sides)
p-4 → padding: 16px (all sides)
p-2 → padding: 8px (all sides)
```

## 3. Border Radius (Bo góc)

```css
rounded-3xl → border-radius: 24px (icon background và card tổng)
rounded-xl  → border-radius: 12px (nút bấm)
```

## 4. Shadow (Bóng đổ)

```css
shadow-lg → box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1),
                         0 4px 6px -2px rgba(0, 0, 0, 0.05)
```

## 5. Font

```css
text-lg     → font-size: 18px; line-height: 28px (Tiêu đề)
text-xs     → font-size: 12px; line-height: 16px (Label)
font-bold   → font-weight: 700
font-medium → font-weight: 500
```

## 6. Layout Grid

```css
grid grid-cols-3 gap-4 →
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
```

## Cách áp dụng vào Android

### 1. Tạo colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="blue_100">#DBEAFE</color>
    <color name="blue_600">#2563EB</color>
    <color name="blue_500">#3B82F6</color>
    <color name="green_100">#D1FAE5</color>
    <color name="green_600">#059669</color>
    <color name="teal_100">#CCFBF1</color>
    <color name="teal_600">#0D9488</color>
    <color name="red_100">#FEE2E2</color>
    <color name="red_600">#DC2626</color>
    <color name="purple_100">#F3E8FF</color>
    <color name="purple_600">#9333EA</color>
    <color name="gray_50">#F9FAFB</color>
    <color name="white">#FFFFFF</color>
    <color name="gray_700">#374151</color>
    <color name="gray_800">#1F2937</color>
</resources>
```

### 2. Tạo dimens.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="icon_container_size">64dp</dimen>
    <dimen name="icon_size">28dp</dimen>
    <dimen name="grid_spacing">16dp</dimen>
    <dimen name="card_padding">24dp</dimen>
    <dimen name="item_padding">8dp</dimen>
    <dimen name="corner_radius_large">24dp</dimen>
    <dimen name="corner_radius_medium">12dp</dimen>
    <dimen name="text_title">18sp</dimen>
    <dimen name="text_label">12sp</dimen>
</resources>
```

### 3. Tạo shape cho icon background (drawable/bg_icon_rounded.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/blue_100"/>
    <corners android:radius="24dp"/>
</shape>
```

### 4. Layout với RecyclerView (activity_main.xml)

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/menuRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="24dp"
    app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
    app:spanCount="3"/>
```

### 5. Item layout (item_menu.xml)

```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="8dp">

    <FrameLayout
        android:id="@+id/iconContainer"
        android:layout_width="64dp"
        android:layout_height="64dp"
        android:background="@drawable/bg_icon_rounded">

        <ImageView
            android:id="@+id/icon"
            android:layout_width="28dp"
            android:layout_height="28dp"
            android:layout_gravity="center"/>
    </FrameLayout>

    <TextView
        android:id="@+id/label"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:textSize="12sp"
        android:fontFamily="sans-serif-medium"
        android:textColor="@color/gray_700"
        android:gravity="center"/>
</LinearLayout>
```

### 6. Data Class Kotlin

```kotlin
data class MenuItem(
    val icon: Int,              // R.drawable.ic_invoice
    val label: String,          // "Hóa đơn"
    val backgroundColor: Int,   // R.color.blue_100
    val iconColor: Int         // R.color.blue_600
)
```

### 7. Danh sách menu items

```kotlin
val menuItems = listOf(
    MenuItem(R.drawable.ic_file_text, "Hóa đơn", R.color.blue_100, R.color.blue_600),
    MenuItem(R.drawable.ic_dollar_sign, "Bảng giá", R.color.green_100, R.color.green_600),
    MenuItem(R.drawable.ic_phone, "Liên hệ", R.color.teal_100, R.color.teal_600),
    MenuItem(R.drawable.ic_map_pin, "Địa chỉ", R.color.red_100, R.color.red_600),
    MenuItem(R.drawable.ic_info, "Giới thiệu", R.color.purple_100, R.color.purple_600),
    MenuItem(R.drawable.ic_mail, "Email", R.color.blue_100, R.color.blue_500)
)
```
