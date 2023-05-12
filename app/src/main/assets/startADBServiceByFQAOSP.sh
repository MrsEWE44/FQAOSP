killall FQAOSPADB
APP_PATH=$(pm path org.fqaosp|cut -d':' -f2)
exec app_process -Djava.class.path="$APP_PATH" /system/bin --nice-name=FQAOSPADB org.fqaosp.service.startADBService >>/dev/null 2>&1 &
echo "run fqtools ok"
