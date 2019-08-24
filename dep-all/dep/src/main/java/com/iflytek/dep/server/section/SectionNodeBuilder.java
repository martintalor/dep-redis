package com.iflytek.dep.server.section;


import com.iflytek.dep.server.config.web.ApplicationContextRegister;
import com.iflytek.dep.server.constants.ActionType;
import com.iflytek.dep.server.constants.ExchangeNodeType;
import com.iflytek.dep.server.constants.PkgType;

/**
 * 构建SectionNode，每个pkg会根据包类型，传输节点类型，以及传输方向不同 构造不同的SectionNode
 *
 * @author Kevin
 */
public class SectionNodeBuilder {
    private PkgType pkgType;
    private ExchangeNodeType exchangeNodeType;
    private ActionType actionType;
    private String packageId;

    public SectionNodeBuilder(PkgType pkgType, ExchangeNodeType exchangeNodeType, ActionType actionType, String packageId) {
        this.pkgType = pkgType;
        this.exchangeNodeType = exchangeNodeType;
        this.actionType = actionType;
        this.packageId = packageId;
    }

    public SectionNode build() {
        //coding build logic here

        switch (actionType) {
            case UP:
                switch (exchangeNodeType) {
                    case LEAF:
                        switch (pkgType) {
                            case ACK:
                                return this.leafAckUpSectionNodeBuild();
                            case DATA:
                                return this.leafPackageUpSectionNodeBuild();
                            default:
                                //非ack和数据包暂时不支持
                                return null;
                        }
                    case MAIN:
                        switch (pkgType) {
                            case ACK:
                                return this.mainAckUpSectionNodeBuild();
                            case DATA:
                                return this.mainPackageUpSectionNodeBuild();
                            case MAINDATA:
                                return this.fromMainPackageUpSectionNodeBuild();
                            default:
                                //非ack和数据包暂时不支持
                                return null;
                        }

                    case ROUTE:
                        switch (pkgType) {
                            case ACK:
                                return this.routeAckUpSectionNodeBuild();
                            case DATA:
                                return this.routePackageUpSectionNodeBuild();
                            default:
                                //非ack和数据包暂时不支持
                                return null;
                        }

                    default:
                        //非子节点、路由节点、中心节点暂不支持
                        return null;
                }


            case DOWN:
                switch (exchangeNodeType) {
                    case LEAF:
                        switch (pkgType) {
                            case ACK:
                                return this.leafAckDownSectionNodeBuild();
                            case DATA:
                                return this.leafPackageDownSectionNodeBuild();
                            default:
                                return null;
                        }

                    case MAIN:
                        switch (pkgType) {
                            case ACK:
                                return this.mainAckDownSectionNodeBuild();
                            case DATA:
                                return this.mainPackageDownSectionNodeBuild();
                            case MAINDATA:
                                return this.mainDataIntoDatabase();
                            default:
                                return null;
                        }

                    case ROUTE:
                        switch (pkgType) {
                            case ACK:
                                return this.routeAckDownSectionNodeBuild();
                            case DATA:
                                return this.routePackageDownSectionNodeBuild();
                            default:
                                return null;
                        }

                    default:
                        return null;
                }

            default:
                return null;
        }

    }

    /**
     * 子节点数据包下载SectionNode构建
     * SectionNode依次是，解压做(包括合包)、移除、入库
     * 中心下载入库小链路
     *
     * @return
     */
    private SectionNode mainDataIntoDatabase() {
        return leafPackageDownSectionNodeBuild();
    }

    /**
     * 子节点数据包上传SectionNode构建
     * SectionNode依次是，打包(包含分包)，加密，上传FTP
     *
     * @return
     */
    private SectionNode leafPackageUpSectionNodeBuild() {
        //文件打包Node
        SectionNode filePackSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //文件加密Node
        SectionNode fileEncryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //文件上传Node
        SectionNode fileUploadSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //文件打包section
        FilePackSection filePackSection = ApplicationContextRegister.getApplicationContext().getBean(FilePackSection.class);
        //加密section
        FileEncryptLeafSection fileEncryptSection = ApplicationContextRegister.getApplicationContext().getBean(FileEncryptLeafSection.class);
        //文件上传section
        FileUploadSection fileUploadSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);


        //section next环节构建
        filePackSectionNode.setNext(fileEncryptSectionNode);
        fileEncryptSectionNode.setNext(fileUploadSectionNode);

        //设置当前Section
        filePackSectionNode.setCurrent(filePackSection);
        fileEncryptSectionNode.setCurrent(fileEncryptSection);
        fileUploadSectionNode.setCurrent(fileUploadSection);

        return filePackSectionNode;
    }

    /**
     * 中心节点数据包上传SectionNode构建
     * SectionNode依次是，打包(包含分包)，加密，上传FTP
     *
     * @return
     */
    private SectionNode fromMainPackageUpSectionNodeBuild(){
        //文件打包Node
        SectionNode filePackSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //文件加密Node
        SectionNode fileEncryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //文件上传Node
        SectionNode fileUploadSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //文件打包section
        FilePackSection filePackSection = ApplicationContextRegister.getApplicationContext().getBean(FilePackSection.class);
        //加密section
        FileEncryptOnlyMainSection fileEncryptSection = ApplicationContextRegister.getApplicationContext().getBean(FileEncryptOnlyMainSection.class);
        //文件上传section
        FileUploadSection fileUploadSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);


        //section next环节构建
        filePackSectionNode.setNext(fileEncryptSectionNode);
        fileEncryptSectionNode.setNext(fileUploadSectionNode);

        //设置当前Section
        filePackSectionNode.setCurrent(filePackSection);
        fileEncryptSectionNode.setCurrent(fileEncryptSection);
        fileUploadSectionNode.setCurrent(fileUploadSection);

        return filePackSectionNode;

    }

    /**
     * 子节点ack包上传SectionNode构建
     * * SectionNode 上传FTP
     *
     * @return
     */
    private SectionNode leafAckUpSectionNodeBuild() {
        //上传Section
        SectionNode fileUploadSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //文件上传section
        FileUploadSection fileUploadSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);
        //为Node设置Section
        fileUploadSectionNode.setCurrent(fileUploadSection);

        return fileUploadSectionNode;


    }

    /**
     * 子节点数据包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包、解密、解压做(包括合包)
     *
     * @return
     */
    private SectionNode leafPackageDownSectionNodeBuild() {
        //下载数据包Node
        SectionNode leafPackageDownSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //解密数据包Node
        SectionNode fileDecryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //解压缩数据包Node
        SectionNode fileUnPackSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //数据包移动Node
        SectionNode leafPackageMoveSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //入库Node
        SectionNode inToDatabaseSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);


        //下载数据包Section
        FileDownloadLeafSection fileDownloadSection = ApplicationContextRegister.getApplicationContext().getBean(FileDownloadLeafSection.class);
        //解密数据包Section
        FileDecryptLeafSection fileDecryptSection = ApplicationContextRegister.getApplicationContext().getBean(FileDecryptLeafSection.class);
        //解压缩数据包Section
        FileUnPackSection fileUnPackSection = ApplicationContextRegister.getApplicationContext().getBean(FileUnPackSection.class);
        //入库Section
        InToDatabaseSection inToDatabaseSection = ApplicationContextRegister.getApplicationContext().getBean(InToDatabaseSection.class);
        //数据包移动Section
        FileMoveSection fileMoveSection = ApplicationContextRegister.getApplicationContext().getBean(FileMoveSection.class);

        //为每个Node设置当前Section
        leafPackageDownSectionNode.setCurrent(fileDownloadSection);
        fileDecryptSectionNode.setCurrent(fileDecryptSection);
        fileUnPackSectionNode.setCurrent(fileUnPackSection);
        inToDatabaseSectionNode.setCurrent(inToDatabaseSection);
        leafPackageMoveSectionNode.setCurrent(fileMoveSection);


        //设置Node的next Node
        leafPackageDownSectionNode.setNext(fileDecryptSectionNode);
        fileDecryptSectionNode.setNext(fileUnPackSectionNode);
        fileUnPackSectionNode.setNext(inToDatabaseSectionNode);
        inToDatabaseSectionNode.setNext(leafPackageMoveSectionNode);

        return leafPackageDownSectionNode;


    }

    /**
     * 子节点Ack包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包
     *
     * @return
     */
    private SectionNode leafAckDownSectionNodeBuild() {

        return null;

    }


    /**
     * 路由节点数据包上传SectionNode构建
     * SectionNode依次是、将数据包上传到ftp上
     *
     * @return
     */
    private SectionNode routePackageUpSectionNodeBuild() {
        return routerSectionBuilder();
    }


    /**
     * 路由节点只需要将数据包先下载再上传即可
     *
     * @return
     */
    private SectionNode routerSectionBuilder() {
        //下载数据包Node
        SectionNode fileDownloadSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //数据包移动Node
        SectionNode leafPackageMoveSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //上传数据包Node
        SectionNode fileUploadSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);


        //下载数据包Node
        FileDownloadLeafSection fileDownloadLeafSectionNode = ApplicationContextRegister.getApplicationContext().getBean(FileDownloadLeafSection.class);
        //上传数据包Section
        FileUploadSection fileUploadSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);
        //数据包移动Section
        FileMoveSection fileMoveSection = ApplicationContextRegister.getApplicationContext().getBean(FileMoveSection.class);


        //为每个Node设置当前Section
        fileDownloadSectionNode.setCurrent(fileDownloadLeafSectionNode);
        fileUploadSectionNode.setCurrent(fileUploadSection);
        leafPackageMoveSectionNode.setCurrent(fileMoveSection);

        //构建链条
        fileDownloadSectionNode.setNext(fileUploadSectionNode);
        leafPackageMoveSectionNode.setNext(leafPackageMoveSectionNode);

        return fileDownloadSectionNode;
    }

    /**
     * 路由节点ack包上传SectionNode构建
     * SectionNode依次是，将数据包上传到ftp上
     *
     * @return
     */
    private SectionNode routeAckUpSectionNodeBuild() {
        return routePackageUpSectionNodeBuild();
    }

    /**
     * 路由节点数据包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包
     *
     * @return
     */
    private SectionNode routePackageDownSectionNodeBuild() {
        return this.leafAckDownSectionNodeBuild();
    }

    /**
     * 路由节点Ack包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包
     *
     * @return
     */
    private SectionNode routeAckDownSectionNodeBuild() {
        return this.leafAckDownSectionNodeBuild();
    }


    /**
     * 中心节点数据包上传SectionNode构建
     * SectionNode依次是，压缩、加密、上传ftp
     *
     * @return
     */
    private SectionNode mainPackageUpSectionNodeBuild() {
        return leafPackageUpSectionNodeBuild();
    }

    /**
     * 中心节点ack包上传SectionNode构建
     * SectionNode依次是，压缩数据包、加密数据包、上传ftp
     *
     * @return
     */
    private SectionNode mainAckUpSectionNodeBuild() {
        return this.routeAckUpSectionNodeBuild();
    }

    /**
     * 中心节点数据包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包、解密、解压缩(包括合包)
     * update by weiyao2 20190611 添加上传后中心入库环节
     * @return
     */
    private SectionNode mainPackageDownSectionNodeBuild() {
//        return mainPacakgeBuilder();
        return mainUnPacakgeBuilder();
    }

    /**
     * 中心数据包builder,下载数据包、解密数据包，加密数据包，上传数据包
     *
     * @return
     */
    private SectionNode mainPacakgeBuilder() {
        //下载数据包Node
        SectionNode packageDownSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //解密数据包Node
        SectionNode fileDecryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //加密数据包Node
        SectionNode fileEncryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //上传数据包Node
        SectionNode filePackageUpSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);
        //数据包移动Node
        SectionNode leafPackageMoveSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);


        //下载数据包Section
        FileDownloadMainSection fileDownloadSection = ApplicationContextRegister.getApplicationContext().getBean(FileDownloadMainSection.class);
        //解密数据包Section
        FileDecryptMainSection fileDecryptSection = ApplicationContextRegister.getApplicationContext().getBean(FileDecryptMainSection.class);
        //加密数据包Section
        FileEncryptMainSection filencryptSection = ApplicationContextRegister.getApplicationContext().getBean(FileEncryptMainSection.class);
        //上传数据包Section
        FileUploadSection fileUPackSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);
        //数据包移动Section
        FileMoveSection fileMoveSection = ApplicationContextRegister.getApplicationContext().getBean(FileMoveSection.class);

        //为每个Node设置当前Section
        packageDownSectionNode.setCurrent(fileDownloadSection);
        fileDecryptSectionNode.setCurrent(fileDecryptSection);
        fileEncryptSectionNode.setCurrent(filencryptSection);
        filePackageUpSectionNode.setCurrent(fileUPackSection);
        leafPackageMoveSectionNode.setCurrent(fileMoveSection);

        //设置Node的next Node
        packageDownSectionNode.setNext(fileDecryptSectionNode);
        fileDecryptSectionNode.setNext(fileEncryptSectionNode);
        fileEncryptSectionNode.setNext(filePackageUpSectionNode);
        filePackageUpSectionNode.setNext(leafPackageMoveSectionNode);

        return packageDownSectionNode;
    }


    /**
     * 从down后开始分叉，判断是中心则copy一个解密后包出来，
     * 在上传完成后进行解压、入库操作，如果中心不是目标，则在section内部判断跳过操作
     * @return
     */
    private SectionNode mainUnPacakgeBuilder() {
        //下载数据包Node
        SectionNode packageDownSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //解密数据包Node
        SectionNode fileDecryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        // copy解密包
        SectionNode fileCopySectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //加密数据包Node
        SectionNode fileEncryptSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //上传数据包Node
        SectionNode filePackageUpSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        // 中心上传完成后判断是否解压Section
        SectionNode mainUnPackSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        // 中心入库section
        SectionNode mainInToDataBaseSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);

        //数据包移动Node
        SectionNode leafPackageMoveSectionNode = ApplicationContextRegister.getApplicationContext().getBean(SectionNode.class);


        //下载数据包Section
        FileDownloadMainSection fileDownloadMainSection = ApplicationContextRegister.getApplicationContext().getBean(FileDownloadMainSection.class);

        // copy解密包
        FileCopyToUnPackMainSection fileCopyToUnPackMainSection = ApplicationContextRegister.getApplicationContext().getBean(FileCopyToUnPackMainSection.class);

        //解密数据包Section
        FileDecryptMainSection fileDecryptMainSection = ApplicationContextRegister.getApplicationContext().getBean(FileDecryptMainSection.class);

        //加密数据包Section
        FileEncryptMainSection fileEncryptMainSection = ApplicationContextRegister.getApplicationContext().getBean(FileEncryptMainSection.class);

        //上传数据包Section
        FileUploadSection fileUpPackSection = ApplicationContextRegister.getApplicationContext().getBean(FileUploadSection.class);

        // 中心上传完成后开始解压
        FileUnPackMainSection fileUnPackMainSection = ApplicationContextRegister.getApplicationContext().getBean(FileUnPackMainSection.class);

        //入库Section
        InToDatabaseSection inToDatabaseSection = ApplicationContextRegister.getApplicationContext().getBean(InToDatabaseSection.class);

        //数据包移动Section
        FileMoveSection fileMoveSection = ApplicationContextRegister.getApplicationContext().getBean(FileMoveSection.class);

        //为每个Node设置当前Section
        packageDownSectionNode.setCurrent(fileDownloadMainSection);// 下载
        fileDecryptSectionNode.setCurrent(fileDecryptMainSection);// 解密
        fileCopySectionNode.setCurrent(fileCopyToUnPackMainSection);// copy解密包，可能无此步骤
        fileEncryptSectionNode.setCurrent(fileEncryptMainSection);// 加密
        filePackageUpSectionNode.setCurrent(fileUpPackSection);// 上传
        mainUnPackSectionNode.setCurrent(fileUnPackMainSection);// 上传后判断是否需要在中心解压
        mainInToDataBaseSectionNode.setCurrent(inToDatabaseSection);// 中心入库，可能无
        leafPackageMoveSectionNode.setCurrent(fileMoveSection);// 移除FTP包

        //设置Node的next Node
        packageDownSectionNode.setNext(fileDecryptSectionNode);
        fileDecryptSectionNode.setNext(fileCopySectionNode);
        fileCopySectionNode.setNext(fileEncryptSectionNode);
        fileEncryptSectionNode.setNext(filePackageUpSectionNode);
        filePackageUpSectionNode.setNext(mainUnPackSectionNode);
        mainUnPackSectionNode.setNext(mainInToDataBaseSectionNode);
        mainInToDataBaseSectionNode.setNext(leafPackageMoveSectionNode);

        return packageDownSectionNode;
    }


    /**
     * 中心节点Ack包下载SectionNode构建
     * SectionNode依次是，从ftp上下载数据包
     *
     * @return
     */
    private SectionNode mainAckDownSectionNodeBuild() {
        return this.routeAckDownSectionNodeBuild();
    }


}
