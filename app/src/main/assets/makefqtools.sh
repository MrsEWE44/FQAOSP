cache_dir_fqaosp="$HOME/fqaosp"
termux_prefix_bin="../$PREFIX/bin/"
fqtools_file="fqtools.tar.xz"
getDepends()
{
   ret=`apt-cache depends $1|grep Depends |cut -d: -f2 |tr -d "<>"`
   echo "$ret"
}

download_depends(){
  if [ ! -d "$cache_dir_fqaosp" ];then
      mkdir -p $cache_dir_fqaosp
  fi
  cd $cache_dir_fqaosp
  libs="android-tools libandroid-posix-semaphore mkbootimg libandroid-support libbz2 libffi $(apt search openjdk|grep jdk|cut -f1 -d'/'|head -n 1) xz-utils"
  i=0
  while [ $i -lt 5 ] ;
  do
      i=$(($i + 1))
      # download libs
      newlist=" "
      for j in $libs
      do
          apt download $j
          added="$(getDepends $j)"
          newlist="$newlist $added"
          apt download $added
      done
      libs=$newlist
  done
}

extract_deb(){
    if [ -d "$cache_dir_fqaosp" ];then
        cd $cache_dir_fqaosp
        for ddd in $(ls *.deb)
        do
            dpkg-deb -x $ddd ./
            rm -rf $ddd
        done
    else
        exit -7
    fi
}

make_git_project(){
    if [ -d "$cache_dir_fqaosp" ];then
        cd $cache_dir_fqaosp
        git clone https://gitee.com/SorryMyLife/fqromtools.git
        cd fqromtools
        pip install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
        pyinstaller -F fqromtools.py
        cp dist/fqromtools "$termux_prefix_bin"
        cd $termux_prefix_bin
        cd ../
        curl -L -O https://gitee.com/SorryMyLife/FQAOSP/releases/download/V1.2.4-ROMBUILD/apktool.jar
    fi
}

make_fqtools(){
    if [ -d "$cache_dir_fqaosp" ];then
        cd  "$cache_dir_fqaosp/.$PREFIX/bin"
        pyinstaller -F mkdtboimg
        rm -rf mkdtboimg
        mv dist/mkdtboimg mkdtboimg
        rm -rf build dist
        cd ../../
        chmod -R 777 usr
        chmod -R u+rw usr
        tar cJvf $fqtools_file usr
        cp $fqtools_file $HOME/storage/downloads/
        if [ -f "$HOME/storage/downloads/$fqtools_file" ];then
            cd $HOME &&rm -rf $cache_dir_fqaosp
            echo "make fqtools ok!"
        else
            echo "make fqtools error!!"
            exit 8
        fi
    fi
}

install_base_tools(){
    apt update && apt upgrade -y && apt install -y python python-pip xz-utils clang ldd binutils-is-llvm git
    pip install -i https://pypi.tuna.tsinghua.edu.cn/simple pyinstaller 
}

main(){
    rm -rf $cache_dir_fqaosp
    install_base_tools
    download_depends
    extract_deb
    make_git_project
    make_fqtools
}
clear
main

