package com.iflytek.dep.server.section;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class FileDecryptSection {
//    private static Logger logger = LoggerFactory.getLogger(FileDecryptSection.class);
//    private static ExecutorService fixedUpStreamThreadPool;
//    public static AtomicInteger threadJobSize = new AtomicInteger(0);
//    @Autowired
//    CreatePackService createPackService;
//    @Autowired
//    UpStatusService upStatusService;
//    @Autowired
//    NodeLinkBeanMapper nodeLinkBeanMapper;
//    @Autowired
//    DataPackBeanMapper dataPackBeanMapper;
//    @Autowired
//    DataPackSubBeanMapper dataPackSubBeanMapper;
//    @Autowired
//    NodeAppBeanMapper nodeAppBeanMapper;
//    @Autowired
//    SectionUtils sectionUtils;
//
//    public FileDecryptSection() {
//        synchronized (logger){
//           if(fixedUpStreamThreadPool == null){
//               fixedUpStreamThreadPool =  Executors.newFixedThreadPool(threadNumber,
//                       new FileDecryptSection.DepThreadFactory("FileDecryptSection"));
//           }
//        }
//    }
//
//    @Override
//    public SectionResult doThreadAct(final String packageId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
//        logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),threadJobSize.incrementAndGet(),packageId,jobId);
//        fixedUpStreamThreadPool.submit(new Runnable() {
//
//            public void run() {
//                try {
//                    logger.info("[{}] , package id [{}] running",this.getClass(),packageId);
//                    sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
//
//                    //获取加密包的路径
//                    String packPath = String.valueOf(map.get("PACK_PATH"));
//                    File file = new File(packPath);
//                    String pagePath = file.getParent();
//                    //获取包名
//                    String packageId = file.getName();
//                    //根据包名截取去那里的appID
//                    String[] appIdTo = PackUtil.splitAppTo(packageId);
//                    //到新的节点找到去往目的地其对应的nodeId
//                    List<String> toNodeId = new ArrayList<String>();
//                    for (String app : appIdTo) {
//                        NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
//                        toNodeId.add(nodeApp.getNodeId());
//                    }
//                    //操作开始时间
//                    Date createTime = new Date();
//                    //参数准备
//                    ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
//                    //解密此路径加密文件
//                    try {
//                        createPackService.decryptZip(packPath);
//                    } catch (DecryptException e) {
//                        map1.put("SEND_STATE_DM", RecvSendStateEnum.FAIL.getStateCode());
//                        e.printStackTrace();
//                        throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(),ExceptionState.DECRYPT.getName()+ packPath);
//                    }
//                    //操作结束时间
//                    Date updateTime = new Date();
//                    //删除加密文件并更改解密文件名称
//                    PackUtil.renameAndDeleteZip(packPath);
//                    //参数准备
//                    List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
//                    //插入主包和子包表
//                    upStatusService.insertPageAndPageSub(file);
//                    if (FileConfigUtil.ISCENTER) {
//                        //若是中心节点
//                        for (String app : toNodeId) {
//                            ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
//                            map1.put("NODE_ID", FileConfigUtil.CURNODEID);
//                            map1.put("PACKAGE_ID", packageId);
//                            map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIEM);
//                            map1.put("TO_NODE_ID", app);
//                            map1.put("CREATE_TIME", createTime);
//                            map1.put("UPDATE_TIME", updateTime);
//                            //插入数据扭转进程表和更新状态表
//                            //插入数据包当前状态表和数据包流水状态表
//                            ConcurrentHashMap<String, Object> resultMap = upStatusService.updateCurState(map1);
//                            String processId = String.valueOf(resultMap.get("PROCESS_ID"));
//                            //构造下个section需要参数
//                            zip.put("FILE_PATH", file.getAbsolutePath());
//                            zip.put("PROCESS_ID", processId);
//                            zip.put("PACKAGE_ID", packageId);
//                            param.add(zip);
//                        }
//
//                    } else {
//                        //非中心节点时
//                        ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
//                        map1.put("NODE_ID", FileConfigUtil.CURNODEID);
//                        map1.put("PACKAGE_ID", packageId);
//                        map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIEMI);
//                        map1.put("TO_NODE_ID", FileConfigUtil.CURNODEID);
//                        map1.put("CREATE_TIME", createTime);
//                        map1.put("UPDATE_TIME", updateTime);
//                        //插入数据扭转进程表和更新状态表
//                        //插入数据包当前状态表和数据包流水状态表
//                        ConcurrentHashMap<String, Object> resultMap = upStatusService.updateCurState(map1);
//                        String processId = String.valueOf(resultMap.get("PROCESS_ID"));
//                        //构造下个section需要参数
//                        zip.put("FILE_PATH", file.getAbsolutePath());
//                        zip.put("PROCESS_ID", processId);
//                        zip.put("PACKAGE_ID", packageId);
//                        param.add(zip);
//                    }
//
//                    map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
//                    map.put("PARAM", param);
//                    map.put("MARK", CommonConstants.STATE.SELF);
//                    //验证有效性并返回结果
//                    boolean valid = PackUtil.isValid(packPath);
//
////                    return SectionResult(valid, map);
//                    if(valid){
//                        sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
//                        SectionNode nextSectionNode = sectionNode.getNext();
//                        if(nextSectionNode!= null){
//                            nextSectionNode.getCurrent().doThreadAct(packageId,jobId,nextSectionNode,totalSectionNumber,map);
//                        }
//                    }
//                } catch (Exception e) {
//                    logger.error("[{}] happend error",this.getClass(), e);
//                } finally {
//                    logger.info("[{}] job has [{}]",this.getClass(),threadJobSize.decrementAndGet());
//                    logger.info("[{}] , package id [{}] run end ",this.getClass(),packageId);
//                }
//            }});
//        return null;
//    }
//    @Override
//    public void update(String pkgId, PkgStatus status) {
//        // update pkg status here
//
//    }
//
//    @Override
//    public SectionResult doAct(ConcurrentHashMap<String, Object> map) throws Exception {
//        //获取加密包的路径
//        String packPath = String.valueOf(map.get("PACK_PATH"));
//        File file = new File(packPath);
//        String pagePath = file.getParent();
//        //获取包名
//        String packageId = file.getName();
//        //根据包名截取去那里的appID
//        String[] appIdTo = PackUtil.splitAppTo(packageId);
//        //到新的节点找到去往目的地其对应的nodeId
//        List<String> toNodeId = new ArrayList<String>();
//        for (String app : appIdTo) {
//            NodeAppBean nodeApp = nodeAppBeanMapper.selectByPrimaryKey(app);
//            toNodeId.add(nodeApp.getNodeId());
//        }
//        //操作开始时间
//        Date createTime = new Date();
//        //参数准备
//        ConcurrentHashMap<String, Object> map1 = new ConcurrentHashMap<String, Object>();
//        //解密此路径加密文件
//        try {
//            createPackService.decryptZip(packPath);
//        } catch (DecryptException e) {
//            map1.put("SEND_STATE_DM", RecvSendStateEnum.FAIL.getStateCode());
//            e.printStackTrace();
//            throw new BusinessErrorException(ExceptionState.DECRYPT.getCode(),ExceptionState.DECRYPT.getName()+ packPath);
//        }
//        //操作结束时间
//        Date updateTime = new Date();
//        //删除加密文件并更改解密文件名称
//        PackUtil.renameAndDeleteZip(packPath);
//        //参数准备
//        List<ConcurrentHashMap<String, Object>> param = new ArrayList<ConcurrentHashMap<String, Object>>();
//        //插入主包和子包表
//        upStatusService.insertPageAndPageSub(file);
//        if (FileConfigUtil.ISCENTER) {
//            //若是中心节点
//            for (String app : toNodeId) {
//                ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
//                map1.put("NODE_ID", FileConfigUtil.CURNODEID);
//                map1.put("PACKAGE_ID", packageId);
//                map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.ZXJIEM);
//                map1.put("TO_NODE_ID", app);
//                map1.put("CREATE_TIME", createTime);
//                map1.put("UPDATE_TIME", updateTime);
//                //插入数据扭转进程表和更新状态表
//                //插入数据包当前状态表和数据包流水状态表
//                ConcurrentHashMap<String, Object> resultMap = upStatusService.updateCurState(map1);
//                String processId = String.valueOf(resultMap.get("PROCESS_ID"));
//                //构造下个section需要参数
//                zip.put("FILE_PATH", file.getAbsolutePath());
//                zip.put("PROCESS_ID", processId);
//                zip.put("PACKAGE_ID", packageId);
//                param.add(zip);
//            }
//
//        } else {
//            //非中心节点时
//            ConcurrentHashMap<String, Object> zip = new ConcurrentHashMap<String, Object>();
//            map1.put("NODE_ID", FileConfigUtil.CURNODEID);
//            map1.put("PACKAGE_ID", packageId);
//            map1.put("OPERATE_STATE_DM", CommonConstants.OPERATESTATE.JIEMI);
//            map1.put("TO_NODE_ID", FileConfigUtil.CURNODEID);
//            map1.put("CREATE_TIME", createTime);
//            map1.put("UPDATE_TIME", updateTime);
//            //插入数据扭转进程表和更新状态表
//            //插入数据包当前状态表和数据包流水状态表
//            ConcurrentHashMap<String, Object> resultMap = upStatusService.updateCurState(map1);
//            String processId = String.valueOf(resultMap.get("PROCESS_ID"));
//            //构造下个section需要参数
//            zip.put("FILE_PATH", file.getAbsolutePath());
//            zip.put("PROCESS_ID", processId);
//            zip.put("PACKAGE_ID", packageId);
//            param.add(zip);
//        }
//
//        map.put("CUR_NODE_ID", FileConfigUtil.CURNODEID);
//        map.put("PARAM", param);
//        map.put("MARK", CommonConstants.STATE.SELF);
//        //验证有效性并返回结果
//        boolean valid = PackUtil.isValid(packPath);
//
//        return SectionResult(valid, map);
//    }
//
//    private SectionResult SectionResult(boolean b, ConcurrentHashMap<String, Object> result) {
//        // TODO Auto-generated method stub
//        SectionResult sectionResult = new SectionResult(b);
//        sectionResult.setMap(result);
//        return sectionResult;
//    }

}
