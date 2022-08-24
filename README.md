# Primochka
Совершает запрос на сервера корпорации Utuak games с целью обновления изображений.
# Primochka manual
## Подключение:

### AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
### Java code
```java
import utuakgames.com.primochka.*;
```

## Использование:

```java
LoadConf.loadConfig();
File path = context.getFilesDir();
UtuakPngLoader upl = new UtuakPngLoader(new IOnPngLoad() {
    @Override
    public void onLoad(int index, String name) {
        File imgFile = new File(path, name);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            runOnUiThread(() -> {
                ImageView image = iviews[index];
                image.setImageBitmap(bitmap);
            });
        }
    }
    @Override
    public void onAllLoad() {
        LoadConf.saveConfig();
    }
}, path);
```

# Primochka API
### UtuakPngLoader
```java
public class UtuakPngLoader
```
Загрузчик изображений с серверов Utuak Games
```java
public UtuakPngLoader(IOnPngLoad onPngLoad, File path)
```
Создаёт загрузчик и определяет события загрузки
```java
public void destroy()
```
Корректно завершает работу потока загрузки
### LoadConf
```java
public class LoadConf
```
Утилита для сохранения данных о загруженных изображениях
```java
public static void saveConfig()
```
Сохраняет данные о загруженных изображениях
```java
public static void loadConfig()
```
Загружает данные о загруженных изображениях
### IOnPngLoad
```java
public interface IOnPngLoad
```
Интерфейс загрузки изображений
```java
void onLoad(int index, String name)
```
Вызывается по окончании загрузки изображения
```java
void onAllLoad()
```
Вызывается по окончании загрузки всех изображений
