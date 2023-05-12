package org.fqaosp.entity;

public class ProcessEntity {

    public ProcessEntity(){}

    @Override
    public String toString() {
        return "ProcessEntity{" +
                "processName='" + processName + '\'' +
                ", umask='" + umask + '\'' +
                ", state='" + state + '\'' +
                ", tgid='" + tgid + '\'' +
                ", ngid='" + ngid + '\'' +
                ", pid='" + pid + '\'' +
                ", ppid='" + ppid + '\'' +
                ", tracerPid='" + tracerPid + '\'' +
                ", uid='" + uid + '\'' +
                ", gid='" + gid + '\'' +
                ", fdsize='" + fdsize + '\'' +
                ", groups='" + groups + '\'' +
                ", vmPeak='" + vmPeak + '\'' +
                ", vmSize='" + vmSize + '\'' +
                ", vmLck='" + vmLck + '\'' +
                ", vmPin='" + vmPin + '\'' +
                ", vmHWM='" + vmHWM + '\'' +
                ", VmRSS='" + VmRSS + '\'' +
                ", rssAnon='" + rssAnon + '\'' +
                ", rssFile='" + rssFile + '\'' +
                ", rssShmem='" + rssShmem + '\'' +
                ", vmData='" + vmData + '\'' +
                ", vmStk='" + vmStk + '\'' +
                ", vmExe='" + vmExe + '\'' +
                ", vmLib='" + vmLib + '\'' +
                ", vmPTE='" + vmPTE + '\'' +
                ", vmSwap='" + vmSwap + '\'' +
                ", coreDumping='" + coreDumping + '\'' +
                ", threads='" + threads + '\'' +
                ", sigq='" + sigq + '\'' +
                ", sigPnd='" + sigPnd + '\'' +
                ", shdPnd='" + shdPnd + '\'' +
                ", sigBlk='" + sigBlk + '\'' +
                ", sigIgn='" + sigIgn + '\'' +
                ", sigGgt='" + sigGgt + '\'' +
                ", capInh='" + capInh + '\'' +
                ", capPrm='" + capPrm + '\'' +
                ", capEff='" + capEff + '\'' +
                ", capBnd='" + capBnd + '\'' +
                ", capAmb='" + capAmb + '\'' +
                ", noNewPrivs='" + noNewPrivs + '\'' +
                ", seccomp='" + seccomp + '\'' +
                ", speculationStoreBypass='" + speculationStoreBypass + '\'' +
                ", cpusAllowed='" + cpusAllowed + '\'' +
                ", cpusAllowedList='" + cpusAllowedList + '\'' +
                ", memsAllowed='" + memsAllowed + '\'' +
                ", memsAllowedList='" + memsAllowedList + '\'' +
                ", voluntaryCtxtSwitches='" + voluntaryCtxtSwitches + '\'' +
                ", nonvoluntaryCtxtSwitches='" + nonvoluntaryCtxtSwitches + '\'' +
                '}';
    }

    private String processName,umask,state,tgid,ngid,pid,ppid
            ,tracerPid,uid,gid,fdsize,groups,vmPeak,vmSize
            ,vmLck,vmPin,vmHWM,VmRSS,rssAnon,rssFile,rssShmem
            ,vmData,vmStk,vmExe,vmLib,vmPTE,vmSwap,coreDumping
            ,threads,sigq,sigPnd,shdPnd,sigBlk,sigIgn,sigGgt,capInh
            ,capPrm,capEff,capBnd,capAmb,noNewPrivs,seccomp,speculationStoreBypass
            ,cpusAllowed,cpusAllowedList,memsAllowed,memsAllowedList,voluntaryCtxtSwitches
            ,nonvoluntaryCtxtSwitches;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getUmask() {
        return umask;
    }

    public void setUmask(String umask) {
        this.umask = umask;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTgid() {
        return tgid;
    }

    public void setTgid(String tgid) {
        this.tgid = tgid;
    }

    public String getNgid() {
        return ngid;
    }

    public void setNgid(String ngid) {
        this.ngid = ngid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public String getTracerPid() {
        return tracerPid;
    }

    public void setTracerPid(String tracerPid) {
        this.tracerPid = tracerPid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getFdsize() {
        return fdsize;
    }

    public void setFdsize(String fdsize) {
        this.fdsize = fdsize;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getVmPeak() {
        return vmPeak;
    }

    public void setVmPeak(String vmPeak) {
        this.vmPeak = vmPeak;
    }

    public String getVmSize() {
        return vmSize;
    }

    public void setVmSize(String vmSize) {
        this.vmSize = vmSize;
    }

    public String getVmLck() {
        return vmLck;
    }

    public void setVmLck(String vmLck) {
        this.vmLck = vmLck;
    }

    public String getVmPin() {
        return vmPin;
    }

    public void setVmPin(String vmPin) {
        this.vmPin = vmPin;
    }

    public String getVmHWM() {
        return vmHWM;
    }

    public void setVmHWM(String vmHWM) {
        this.vmHWM = vmHWM;
    }

    public String getVmRSS() {
        return VmRSS;
    }

    public void setVmRSS(String vmRSS) {
        VmRSS = vmRSS;
    }

    public String getRssAnon() {
        return rssAnon;
    }

    public void setRssAnon(String rssAnon) {
        this.rssAnon = rssAnon;
    }

    public String getRssFile() {
        return rssFile;
    }

    public void setRssFile(String rssFile) {
        this.rssFile = rssFile;
    }

    public String getRssShmem() {
        return rssShmem;
    }

    public void setRssShmem(String rssShmem) {
        this.rssShmem = rssShmem;
    }

    public String getVmData() {
        return vmData;
    }

    public void setVmData(String vmData) {
        this.vmData = vmData;
    }

    public String getVmStk() {
        return vmStk;
    }

    public void setVmStk(String vmStk) {
        this.vmStk = vmStk;
    }

    public String getVmExe() {
        return vmExe;
    }

    public void setVmExe(String vmExe) {
        this.vmExe = vmExe;
    }

    public String getVmLib() {
        return vmLib;
    }

    public void setVmLib(String vmLib) {
        this.vmLib = vmLib;
    }

    public String getVmPTE() {
        return vmPTE;
    }

    public void setVmPTE(String vmPTE) {
        this.vmPTE = vmPTE;
    }

    public String getVmSwap() {
        return vmSwap;
    }

    public void setVmSwap(String vmSwap) {
        this.vmSwap = vmSwap;
    }

    public String getCoreDumping() {
        return coreDumping;
    }

    public void setCoreDumping(String coreDumping) {
        this.coreDumping = coreDumping;
    }

    public String getThreads() {
        return threads;
    }

    public void setThreads(String threads) {
        this.threads = threads;
    }

    public String getSigq() {
        return sigq;
    }

    public void setSigq(String sigq) {
        this.sigq = sigq;
    }

    public String getSigPnd() {
        return sigPnd;
    }

    public void setSigPnd(String sigPnd) {
        this.sigPnd = sigPnd;
    }

    public String getShdPnd() {
        return shdPnd;
    }

    public void setShdPnd(String shdPnd) {
        this.shdPnd = shdPnd;
    }

    public String getSigBlk() {
        return sigBlk;
    }

    public void setSigBlk(String sigBlk) {
        this.sigBlk = sigBlk;
    }

    public String getSigIgn() {
        return sigIgn;
    }

    public void setSigIgn(String sigIgn) {
        this.sigIgn = sigIgn;
    }

    public String getSigGgt() {
        return sigGgt;
    }

    public void setSigGgt(String sigGgt) {
        this.sigGgt = sigGgt;
    }

    public String getCapInh() {
        return capInh;
    }

    public void setCapInh(String capInh) {
        this.capInh = capInh;
    }

    public String getCapPrm() {
        return capPrm;
    }

    public void setCapPrm(String capPrm) {
        this.capPrm = capPrm;
    }

    public String getCapEff() {
        return capEff;
    }

    public void setCapEff(String capEff) {
        this.capEff = capEff;
    }

    public String getCapBnd() {
        return capBnd;
    }

    public void setCapBnd(String capBnd) {
        this.capBnd = capBnd;
    }

    public String getCapAmb() {
        return capAmb;
    }

    public void setCapAmb(String capAmb) {
        this.capAmb = capAmb;
    }

    public String getNoNewPrivs() {
        return noNewPrivs;
    }

    public void setNoNewPrivs(String noNewPrivs) {
        this.noNewPrivs = noNewPrivs;
    }

    public String getSeccomp() {
        return seccomp;
    }

    public void setSeccomp(String seccomp) {
        this.seccomp = seccomp;
    }

    public String getSpeculationStoreBypass() {
        return speculationStoreBypass;
    }

    public void setSpeculationStoreBypass(String speculationStoreBypass) {
        this.speculationStoreBypass = speculationStoreBypass;
    }

    public String getCpusAllowed() {
        return cpusAllowed;
    }

    public void setCpusAllowed(String cpusAllowed) {
        this.cpusAllowed = cpusAllowed;
    }

    public String getCpusAllowedList() {
        return cpusAllowedList;
    }

    public void setCpusAllowedList(String cpusAllowedList) {
        this.cpusAllowedList = cpusAllowedList;
    }

    public String getMemsAllowed() {
        return memsAllowed;
    }

    public void setMemsAllowed(String memsAllowed) {
        this.memsAllowed = memsAllowed;
    }

    public String getMemsAllowedList() {
        return memsAllowedList;
    }

    public void setMemsAllowedList(String memsAllowedList) {
        this.memsAllowedList = memsAllowedList;
    }

    public String getVoluntaryCtxtSwitches() {
        return voluntaryCtxtSwitches;
    }

    public void setVoluntaryCtxtSwitches(String voluntaryCtxtSwitches) {
        this.voluntaryCtxtSwitches = voluntaryCtxtSwitches;
    }

    public String getNonvoluntaryCtxtSwitches() {
        return nonvoluntaryCtxtSwitches;
    }

    public void setNonvoluntaryCtxtSwitches(String nonvoluntaryCtxtSwitches) {
        this.nonvoluntaryCtxtSwitches = nonvoluntaryCtxtSwitches;
    }

    public ProcessEntity(String processName, String umask, String state, String tgid, String ngid, String pid, String ppid, String tracerPid, String uid, String gid, String fdsize, String groups, String vmPeak, String vmSize, String vmLck, String vmPin, String vmHWM, String vmRSS, String rssAnon, String rssFile, String rssShmem, String vmData, String vmStk, String vmExe, String vmLib, String vmPTE, String vmSwap, String coreDumping, String threads, String sigq, String sigPnd, String shdPnd, String sigBlk, String sigIgn, String sigGgt, String capInh, String capPrm, String capEff, String capBnd, String capAmb, String noNewPrivs, String seccomp, String speculationStoreBypass, String cpusAllowed, String cpusAllowedList, String memsAllowed, String memsAllowedList, String voluntaryCtxtSwitches, String nonvoluntaryCtxtSwitches) {
        this.processName = processName;
        this.umask = umask;
        this.state = state;
        this.tgid = tgid;
        this.ngid = ngid;
        this.pid = pid;
        this.ppid = ppid;
        this.tracerPid = tracerPid;
        this.uid = uid;
        this.gid = gid;
        this.fdsize = fdsize;
        this.groups = groups;
        this.vmPeak = vmPeak;
        this.vmSize = vmSize;
        this.vmLck = vmLck;
        this.vmPin = vmPin;
        this.vmHWM = vmHWM;
        VmRSS = vmRSS;
        this.rssAnon = rssAnon;
        this.rssFile = rssFile;
        this.rssShmem = rssShmem;
        this.vmData = vmData;
        this.vmStk = vmStk;
        this.vmExe = vmExe;
        this.vmLib = vmLib;
        this.vmPTE = vmPTE;
        this.vmSwap = vmSwap;
        this.coreDumping = coreDumping;
        this.threads = threads;
        this.sigq = sigq;
        this.sigPnd = sigPnd;
        this.shdPnd = shdPnd;
        this.sigBlk = sigBlk;
        this.sigIgn = sigIgn;
        this.sigGgt = sigGgt;
        this.capInh = capInh;
        this.capPrm = capPrm;
        this.capEff = capEff;
        this.capBnd = capBnd;
        this.capAmb = capAmb;
        this.noNewPrivs = noNewPrivs;
        this.seccomp = seccomp;
        this.speculationStoreBypass = speculationStoreBypass;
        this.cpusAllowed = cpusAllowed;
        this.cpusAllowedList = cpusAllowedList;
        this.memsAllowed = memsAllowed;
        this.memsAllowedList = memsAllowedList;
        this.voluntaryCtxtSwitches = voluntaryCtxtSwitches;
        this.nonvoluntaryCtxtSwitches = nonvoluntaryCtxtSwitches;
    }
}
