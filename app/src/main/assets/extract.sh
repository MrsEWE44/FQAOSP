FQTOOLS_NAME="fqtools.tar.xz"
if [ -f "busybox" ];then
	chmod 777 busybox
	if [ -f "$FQTOOLS_NAME" ];then
		./busybox tar xf "$FQTOOLS_NAME"
		rm -rf $FQTOOLS_NAME
		touch fqtools
		exit 0;
	fi
fi


