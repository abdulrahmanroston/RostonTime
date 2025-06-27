# 🚀 دليل إعداد Roston Time على GitHub

## 📋 الخطوات المطلوبة:

### 1️⃣ إنشاء Repository جديد:
\`\`\`bash
1. اذهب إلى https://github.com
2. اضغط على "New Repository"
3. اسم المشروع: roston-time-android
4. اجعله Public ✅
5. اضغط "Create Repository"
\`\`\`

### 2️⃣ رفع الملفات:
\`\`\`bash
# طريقة 1: استخدام GitHub Web Interface
1. اضغط "uploading an existing file"
2. اسحب جميع الملفات والمجلدات
3. اكتب رسالة: "Initial commit - Roston Time App"
4. اضغط "Commit changes"

# طريقة 2: استخدام Git Command Line
git clone https://github.com/YOUR_USERNAME/roston-time-android.git
cd roston-time-android
# انسخ جميع الملفات هنا
git add .
git commit -m "Initial commit - Roston Time App"
git push origin main
\`\`\`

### 3️⃣ تفعيل GitHub Actions:
\`\`\`bash
1. اذهب إلى تبويب "Actions" في المشروع
2. اضغط "I understand my workflows, go ahead and enable them"
3. انتظر بدء البناء التلقائي (5-10 دقائق)
\`\`\`

### 4️⃣ الحصول على رابط التحميل:
\`\`\`bash
1. بعد انتهاء البناء، اذهب إلى "Releases"
2. ستجد إصدار جديد مع رابط تحميل APK
3. انسخ الرابط وشاركه مع أي شخص
\`\`\`

## 🔧 حل المشاكل الشائعة:

### ❌ فشل البناء:
\`\`\`bash
# تحقق من:
1. وجود جميع الملفات في المكان الصحيح
2. صحة ملف build.gradle.kts
3. وجود ملف gradle-wrapper.properties
\`\`\`

### ❌ لا يظهر تبويب Releases:
\`\`\`bash
# الحل:
1. تأكد من أن البناء نجح
2. تحقق من وجود ملف .github/workflows/build.yml
3. انتظر انتهاء البناء كاملاً
\`\`\`

### ❌ APK لا يعمل:
\`\`\`bash
# تحقق من:
1. إصدار Android (يجب أن يكون 7.0+)
2. تفعيل "مصادر غير معروفة"
3. منح الصلاحيات المطلوبة
\`\`\`

## 📱 رابط التحميل النهائي:
بعد اتباع الخطوات، ستحصل على رابط مثل:
\`\`\`
https://github.com/YOUR_USERNAME/roston-time-android/releases/latest/download/roston-time-latest.apk
\`\`\`

## 🎯 نصائح إضافية:

### تحديث التطبيق:
- أي تغيير في الكود سيؤدي لبناء إصدار جديد تلقائياً
- رقم الإصدار يزيد تلقائياً

### مشاركة التطبيق:
- الرابط ثابت ويمكن مشاركته مع أي شخص
- التحديثات تظهر تلقائياً في نفس الرابط

### الأمان:
- التطبيق موقع رقمياً من GitHub
- آمن للتحميل والاستخدام
