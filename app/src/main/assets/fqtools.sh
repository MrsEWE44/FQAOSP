#!/system/bin/sh
fqaosp_home="/data/data/org.fqaosp"
fqaosp_usr="$fqaosp_home/files/usr"
data_local_tmp="/data/local/tmp"
apks_tmp_dir_path="$data_local_tmp/apks"
backup_app_home="/sdcard/backup_app"
sdcard_android_path="/sdcard/Android"
busybox_my="$fqaosp_home/files/busybox"
xz_my="$fqaosp_usr/bin/xz"
brotli_my="$fqaosp_usr/bin/brotli"
xz_parm="-9 -T 6 -z"
brotli_parm="-f -j -q 11"
export TMPDIR="$fqaosp_home/cache"
export JAVA_HOME="$fqaosp_usr/opt/openjdk"
export PATH=$PATH:"$fqaosp_usr/bin":"$JAVA_HOME/bin"
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"$fqaosp_usr/lib":"$JAVA_HOME/lib"

install_apks(){
	apks_path=$1
	if [ -f "$apks_path" ];then
		rm -rf $apks_tmp_dir_path
		mkdir -p $apks_tmp_dir_path
		$busybox_my unzip -d $apks_tmp_dir_path $apks_path
		cd $apks_tmp_dir_path/splits/
		install_split_apks
		rm -rf $apks_tmp_dir_path && exit 0;
	else
		echo "$apks_path not exists"
		help_msg
		exit 1
	fi
}

install_apk_on_path(){
	apk_dir_path=$1
	if [ -d "$apk_dir_path" ];then
		cd $apk_dir_path/
		apk_files=$(find -name "*.apk" -o -name "*.apks"|xargs dirname|uniq)
		if [ $apk_files == "." ];then
			find -name "*.apk" -o -name "*.apks"|while read apkff 
			do
				echo "apkff :::: $apkff"
				if [[ `echo $apkff |grep ".apks"` != "" ]];then
					install_apks "./$apkff"
				else
					cp "$apkff" $data_local_tmp/base.apk && pm install $data_local_tmp/base.apk && rm -rf $data_local_tmp/base.apk
				fi
			done
		else
			for apkd in $apk_files
			do
				cd $apkd 
				apks_sum=`find -name "*.apk" |wc -l`
				apkff=`find -name "*.apk" -o -name "*.apks"`
				if [[ `echo $apkff |grep ".apks"` != "" ]];then
					install_apks "./$apkff"
				fi
				if [ $apks_sum -gt 1 ];then
					install_split_apks
				else
					cp $apkff $data_local_tmp/base.apk && pm install $data_local_tmp/base.apk && rm -rf $data_local_tmp/base.apk
				fi
				cd $apk_dir_path/
			done
		fi
		
		
		exit 0;
	else
		echo "$apk_dir_path not exists"
		help_msg
		exit 1
	fi
}

install_split_apks(){
	total=0
	for aa in $(ls *.apk)
	do
		o=($(ls -l $aa))
		let total=$total+${o[4]}
	done
	#echo "total size $total"
	create=$(pm install-create -S $total)
	sid=$(echo $create |grep -E -o '[0-9]+')
	#echo "session id $sid"
	for aa in $(ls *.apk)
	do
		o=($(ls -l $aa))
		#echo "write $aa to $sid"
		cat $aa | pm install-write -S ${o[4]} $sid $aa -
	done
	pm install-commit $sid
}

backup_app(){
	pkgname=$1
	backup_mode=$2
	backup_type=$3
	apk_file_path=$(pm path $pkgname | head -n 1 |cut -d ':'  -f2 |cut -d '/' -f4)
	out_dir_path="$backup_app_home/$pkgname"
	tar_parm=""
	fffend=""
	xz_cmdstr=""
	mkdir -p $out_dir_path
	case $backup_type in 
		txz)
			tar_parm="cf"
			fffend="tar"
			xz_cmdstr="$xz_my $xz_parm "
		break;;
		tbz)
			tar_parm="jcf"
			fffend="tar.bz2"
			xz_cmdstr="echo "
		break;;
		tgz)
			tar_parm="zcf"
			fffend="tar.gz"
			xz_cmdstr="echo "
		break;;
    tbr)
      tar_parm="cf"
      fffend="tar"
      xz_cmdstr="$brotli_my $brotli_parm "
      break;;
		*)
		help_msg
		break;;
	esac
	case $backup_mode in 
		full)
			cd /data/app && $busybox_my tar "$tar_parm" "$out_dir_path/file.${fffend}" $apk_file_path && $xz_cmdstr "$out_dir_path/file.${fffend}"
			cd /data/data
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/data.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/data.${fffend}"
			else
				cd /proc/1/cwd/data/data
				if [ -d $pkgname ];then
					$busybox_my tar "$tar_parm" "$out_dir_path/data.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/data.${fffend}"
			
				else
					echo "Check if you use namespace isolation for root commands in Magisk (other root solutions might have this, too)."
					exit -1;
				fi
			fi
			cd "$sdcard_android_path/data"
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/sddata.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/sddata.${fffend}"
			fi
			cd "$sdcard_android_path/obb"
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/sdobb.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/sdobb.${fffend}"
			fi
			cd $out_dir_path
			cd ../
			$busybox_my tar "$tar_parm" "$pkgname.${fffend}" $pkgname && $xz_cmdstr "$pkgname.${fffend}" && rm -rf $pkgname && exit 0;
		
		break;;
		data)
			cd /data/data
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/data.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/data.${fffend}"
			else
				cd /proc/1/cwd/data/data
				if [ -d $pkgname ];then
					$busybox_my tar "$tar_parm" "$out_dir_path/data.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/data.${fffend}"
				else
					echo "Check if you use namespace isolation for root commands in Magisk (other root solutions might have this, too)."
					exit -1;
				fi
			fi
			cd "$sdcard_android_path/data"
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/sddata.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/sddata.${fffend}"
			fi
			cd "$sdcard_android_path/obb"
			if [ -d $pkgname ];then
				$busybox_my tar "$tar_parm" "$out_dir_path/sdobb.${fffend}" $pkgname && $xz_cmdstr "$out_dir_path/sdobb.${fffend}"
			fi
			cd $out_dir_path
			cd ../
			$busybox_my tar "$tar_parm" "$pkgname.${fffend}" $pkgname && $xz_cmdstr "$pkgname.${fffend}" && rm -rf $pkgname && exit 0;
			
		break;;
		apk)
			cd /data/app && $busybox_my tar "$tar_parm" "$out_dir_path/file.${fffend}" $apk_file_path && $xz_cmdstr "$out_dir_path/file.${fffend}"
			cd $out_dir_path
			cd ../
			$busybox_my tar "$tar_parm" "$pkgname.${fffend}" $pkgname && $xz_cmdstr "$pkgname.${fffend}" && rm -rf $pkgname && exit 0;
			
		break;;
		*)
		help_msg
		break;;
	esac
}

restory_app(){
	pkgname=$1
	backup_mode=$2
	backup_type=$3
	r_fffend=""
	cd $backup_app_home
	case $backup_type in
		txz)
			r_fffend="tar.xz"
		break;;
		tbz)
			r_fffend="tar.bz2"
		break;;
		tgz)
			r_fffend="tar.gz"
		break;;
    tbr)
      r_fffend="tar.br"
    break;;
		*)
		help_msg
		break;;
	esac
	case $backup_mode in
		full)
			if [ -f "$pkgname.${r_fffend}" ];then
				if [ "$backup_type" == "tbr" ];then
				  $brotli_my -d "$pkgname.${r_fffend}" && $busybox_my tar xf "$pkgname.tar"
				else
				  $busybox_my tar xf "$pkgname.${r_fffend}"
				fi
				cd $pkgname
				if [ -f "file.${r_fffend}" ] && [ -f "data.${r_fffend}" ];then
				  mkdir file
				  if [ "$backup_type" == "tbr" ];then
              $brotli_my -d "file.${r_fffend}" && $busybox_my tar xf "file.tar" -C file/
          else
          	  $busybox_my tar xf "file.${r_fffend}" -C file/
          fi
					cd file && cd `find -name "base.apk" |xargs dirname` 
					apks_sum=$(find -name "*.apk" |wc -l)
					if [ $apks_sum -gt 1 ];then
						install_split_apks
					else
						cp base.apk $data_local_tmp/ && pm install $data_local_tmp/base.apk && rm -rf $data_local_tmp/base.apk
					fi
					in_app_uid=$(pm list packages -U $pkgname |cut -d ':' -f 3)
					cd "$backup_app_home/$pkgname"
					pkg_data_dir="/data/data"
					pkg_dir_path="$pkg_data_dir/$pkgname"
					uid_dir_path="/proc/1/cwd/data/data"
					if [ -d $pkg_dir_path ];then
						pkg_dir_path="$pkg_data_dir/$pkgname"
					else
						pkg_dir_path="$uid_dir_path/$pkgname"
						pkg_data_dir="$uid_dir_path"
					fi
					LIB_LINK=""
					if [ -h "$pkg_dir_path/lib" ];then
						LIB_LINK=`$busybox_my ls -l $pkg_dir_path/lib|$busybox_my awk '{print $11}'`
					fi
					CACHE_UID=""
					if [ -d "$pkg_dir_path/cache" ];then
						for cc in $(ls -l $pkg_dir_path/ |$busybox_my grep "cache" |$busybox_my awk '{print $4}')
						do
							CACHE_UID=$cc
							break;
						done
					fi
					if [ "$backup_type" == "tbr" ];then
              $brotli_my -d "data.${r_fffend}" && $busybox_my tar xf "data.tar" -mokC $pkg_data_dir
          else
          	  $busybox_my tar xf "data.${r_fffend}" -mokC $pkg_data_dir
          fi
					for dddf in $($busybox_my ls -l $pkg_dir_path|$busybox_my awk '$3=="root" {print $9}')
					do
						if [ $dddf != "lib" ];then
							chown -R $in_app_uid:$in_app_uid $pkg_dir_path/$dddf
						fi
					done
					if [ -f "sddata.${r_fffend}" ];then
					  if [ "$backup_type" == "tbr" ];then
                $brotli_my -d "sddata.${r_fffend}" && $busybox_my tar xf "sddata.tar" -C $sdcard_android_path/data/
            else
                $busybox_my tar xf "sddata.${r_fffend}" -C $sdcard_android_path/data/
            fi
					fi
					if [ -f "sdobb.${r_fffend}" ];then
					  if [ "$backup_type" == "tbr" ];then
                $brotli_my -d "sdobb.${r_fffend}" && $busybox_my tar xf "sdobb.${r_fffend}" -C $sdcard_android_path/obb/
            else
                $busybox_my tar xf "sdobb.${r_fffend}" -C $sdcard_android_path/obb/
            fi
					fi
					cd ../
					rm -rf $pkgname && exit 0;
				else
					echo "$pkgname restory error! not found file and data $r_fffend"
					help_msg
					exit 1
				fi
			fi	
			
		break;;
		data)
			if [ -f "$pkgname.${r_fffend}" ];then
			  if [ "$backup_type" == "tbr" ];then
          $brotli_my -d "$pkgname.${r_fffend}" && $busybox_my tar xf "$pkgname.tar"
        else
          $busybox_my tar xf "$pkgname.${r_fffend}"
        fi
				cd $pkgname
				if [ -f "data.${r_fffend}" ];then
					in_app_uid=$(pm list packages -U $pkgname |cut -d ':' -f 3)
					cd "$backup_app_home/$pkgname"
					pkg_data_dir="/data/data"
					pkg_dir_path="$pkg_data_dir/$pkgname"
					uid_dir_path="/proc/1/cwd/data/data"
					if [ -d $pkg_dir_path ];then
						pkg_dir_path="$pkg_data_dir/$pkgname"
					else
						pkg_dir_path="$uid_dir_path/$pkgname"
						pkg_data_dir="$uid_dir_path"
					fi
					LIB_LINK=""
					if [ -h "$pkg_dir_path/lib" ];then
						LIB_LINK=`$busybox_my ls -l $pkg_dir_path/lib|$busybox_my awk '{print $11}'`
					fi
					CACHE_UID=""
					if [ -d "$pkg_dir_path/cache" ];then
						for cc in $(ls -l $pkg_dir_path/ |$busybox_my grep "cache" |$busybox_my awk '{print $4}')
						do
							CACHE_UID=$cc
							break;
						done
					fi
					if [ "$backup_type" == "tbr" ];then
              $brotli_my -d "data.${r_fffend}" && $busybox_my tar xf "data.tar" -mokC $pkg_data_dir
          else
              $busybox_my tar xf "data.${r_fffend}" -mokC $pkg_data_dir
          fi
					for dddf in $($busybox_my ls -l $pkg_dir_path|$busybox_my awk '$3=="root" {print $9}')
					do
						if [ $dddf != "lib" ];then
							chown -R $in_app_uid:$in_app_uid $pkg_dir_path/$dddf
						fi
					done
					if [ -f "sddata.${r_fffend}" ];then
            if [ "$backup_type" == "tbr" ];then
                $brotli_my -d "sddata.${r_fffend}" && $busybox_my tar xf "sddata.tar" -C $sdcard_android_path/data/
            else
                $busybox_my tar xf "sddata.${r_fffend}" -C $sdcard_android_path/data/
            fi
          fi
          if [ -f "sdobb.${r_fffend}" ];then
            if [ "$backup_type" == "tbr" ];then
                $brotli_my -d "sdobb.${r_fffend}" && $busybox_my tar xf "sdobb.${r_fffend}" -C $sdcard_android_path/obb/
            else
                $busybox_my tar xf "sdobb.${r_fffend}" -C $sdcard_android_path/obb/
            fi
          fi
					cd ../
					rm -rf $pkgname && exit 0;
				else
					echo "$pkgname restory error! not found file and data $r_fffend"
					help_msg
					exit 1
				fi
			fi	
		break;;
		apk)
			if [ -f "$pkgname.${r_fffend}" ];then
			  if [ "$backup_type" == "tbr" ];then
          $brotli_my -d "$pkgname.${r_fffend}" && $busybox_my tar xf "$pkgname.tar"
        else
          $busybox_my tar xf "$pkgname.${r_fffend}"
        fi
				cd $pkgname
				if [ -f "file.${r_fffend}" ];then
				  mkdir file
          if [ "$backup_type" == "tbr" ];then
              $brotli_my -d "file.${r_fffend}" && $busybox_my tar xf "file.tar" -C file/
          else
              $busybox_my tar xf "file.${r_fffend}" -C file/
          fi
					cd file && cd `find -name "base.apk" |xargs dirname` 
					apks_sum=$(find -name "*.apk" |wc -l)
					if [ $apks_sum -gt 1 ];then
						install_split_apks
					else
						cp base.apk $data_local_tmp/ && pm install $data_local_tmp/base.apk && rm -rf $data_local_tmp/base.apk
					fi
					cd ../
					rm -rf $pkgname && exit 0;
				else
					echo "$pkgname restory error! not found file and data $r_fffend"
					help_msg
					exit 1
				fi
			fi	
		break;;
		*)
		help_msg
		break;;
	esac

}

unpack_rom_img(){
	ROMTYPE="$1"
	ROMFULLPATH="$2"
	ROMOUTPATH="$3"
	ROMPARTTYPE="$4"
	if [ -f "$ROMFULLPATH" ];then
	  FILE_NAME=`basename -s .img $ROMFULLPATH`
	  if [ ! -d "$ROMOUTPATH" ];then
    	mkdir -p "$ROMOUTPATH"
    fi
    TRANSFER_LIST=""
    OUTIMGNAME=""
    case $ROMPARTTYPE in
      product)
        TRANSFER_LIST="product.transfer.list"
        OUTIMGNAME="product.img"
        break;;
      vendor)
        TRANSFER_LIST="vendor.transfer.list"
        OUTIMGNAME="vendor.img"
        break;;
      system)
        TRANSFER_LIST="system.transfer.list"
        OUTIMGNAME="system.img"
        break;;
    esac
	  case $ROMTYPE in
    	  paybin)
    	    fqromtools --tool payload --inputfile "$ROMFULLPATH" --out "$ROMOUTPATH/" && exit 0
    	    break;;
    	  sndat)
    	    fqromtools --tool sdat2img --transferlist "$(dirname $ROMFULLPATH)/$TRANSFER_LIST" --inputfile $ROMFULLPATH --out "$ROMOUTPATH/$OUTIMGNAME" && exit 0
    	    break;;
    	  sndatbr)
    	    brotli -d $ROMFULLPATH -o "$ROMOUTPATH/tmp.new.dat" -f && fqromtools --tool sdat2img --transferlist "$(dirname $ROMFULLPATH)/$TRANSFER_LIST" --inputfile "$ROMOUTPATH/tmp.new.dat" --out "$ROMOUTPATH/$OUTIMGNAME" && rm -rf "$ROMOUTPATH/tmp.new.dat" && exit 0
    	    break;;
    	  super)
    	    simg2img "$ROMFULLPATH" "$ROMOUTPATH/tmp.img" && lpunpack "$ROMOUTPATH/tmp.img" "$ROMOUTPATH/" && rm -rf "$ROMOUTPATH/tmp.img" && exit 0
    	    break;;
    	esac
  else
    echo "file no exists -- $ROMFULLPATH"
    exit -4
	fi
}

repack_rom_img(){
	IMGFULLPATH="$1"
	REPACKOUTPATH="$2"
	REPACKTYPE="$3"
	ANDROIDLEVEL="$4"
	if [ -f "$IMGFULLPATH" ];then
	  if [ ! -d "$REPACKOUTPATH" ];then
	    mkdir -p "$REPACKOUTPATH"
	  fi
	  case $REPACKTYPE in
	    sdat)
	      img2simg "$IMGFULLPATH" "$REPACKOUTPATH/tmp.img"  && fqromtools --tool img2sdat --inputfile "$REPACKOUTPATH/tmp.img" --out "$REPACKOUTPATH/" --androidversion "$ANDROIDLEVEL" && rm -rf "$REPACKOUTPATH/tmp.img" && exit 0
	      break;;
	    sdatbr)
	      img2simg "$IMGFULLPATH" "$REPACKOUTPATH/tmp.img"  && fqromtools --tool img2sdat --inputfile  "$REPACKOUTPATH/tmp.img" -out "$REPACKOUTPATH/" --androidversion "$ANDROIDLEVEL" && rm -rf "$REPACKOUTPATH/tmp.img" && brotli -f -q 11 "$REPACKOUTPATH/system.new.dat" && exit 0
	      break;;
	    sparseimg)
	      img2simg "$IMGFULLPATH" "$REPACKOUTPATH/sparse.img"  && exit 0
	      break;;
	  esac
  else
    echo "file no exists -- $IMGFULLPATH"
    exit -4
	fi
}

apk_tool(){
  APKTOOLOPT="$1"
  APKFULLPATH="$2"
  OUTPATH="$3"
  case $APKTOOLOPT in
    deapk)
      java -jar usr/apktool.jar -r d -o "$OUTPATH" "$APKFULLPATH"
      break;;
    reapk)
      java -jar usr/apktool.jar b -o "$OUTPATH" "$APKFULLPATH"
      break;;
  esac

}

help_msg(){
	echo "$0 [arg1] [arg2] [arg3] [arg4] \r\n"
	echo -ne "inapks [arg2] : install local apks file.[arg2] : local apks file path\r\nbackup [arg2] [arg3] [arg4] : backup local app and data.[arg2] : local installed package name\r\nrestory [arg2] [arg3] [arg4] : restory local backup app.tar.[arg2] : restory package name\r\ninapkonpath [arg2] : install apk files on path.\r\n\r\n"
	echo -ne "[arg3] : backup mode {full,data,apk}\r\n full : backup apk && data && obb. all files \r\n data : backup apk data and obb . no include apk file. \r\n apk : backup apk file . no include other files\r\n\r\n"
	echo -ne "[arg4] : backup type {txz,tgz,tbz}\r\n txz : compression or decompression tar.xz \r\n tgz : compression or decompression tar.gz \r\n tbz : compression or decompression tar.bz2 \r\n\r\n "
	exit 1;
}

if [ -f $busybox_my ];then
	mmm=$1
	pkgname=$2
	backup_mode=$3
	backup_type=$4
	parm5=$5
	case $mmm in
		inapks)
			install_apks $pkgname
		break;;
		backup)
			backup_app $pkgname $backup_mode $backup_type
		break;;
		restory)
			restory_app $pkgname $backup_mode $backup_type
		break;;
		inapkonpath)
			install_apk_on_path $pkgname
		break;;
		unpackrom)
			unpack_rom_img $pkgname $backup_mode $backup_type $parm5
		break;;
    apktool)
      apk_tool $pkgname $backup_mode $backup_type
    break;;
    repackrom)
      repack_rom_img $pkgname $backup_mode $backup_type $parm5
    break;;
		*)
		help_msg
		break;;
	esac
else
	echo "need install busybox"
	exit -1;
fi
