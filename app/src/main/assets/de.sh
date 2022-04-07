OUTDIRNAME="$1"
INPUTFILEPATH="$2"
jdkfile="jdk.tar.xz"
apktoolfile="apktool.jar"
jdkdir="jdk"
javacmd="$jdkdir/java.sh"

if [ -f "busybox" ] && [ -f "$apktoolfile" ] && [ -f "$jdkfile" ];then
		chmod 777 busybox
		if [ ! -d "$jdkdir" ];then
			./busybox tar xf "$jdkfile"
			rm -rf "$jdkfile"
			chmod -R 777 "$jdkdir"
		fi
fi

if [ -d "$jdkdir" ] && [ -f "$javacmd" ];then
	cd "$jdkdir" && sh java.sh -jar ../apktool.jar d -o "$OUTDIRNAME" "$INPUTFILEPATH"
fi