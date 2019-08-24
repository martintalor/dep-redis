package com.iflytek.dep.server.section;


import org.springframework.stereotype.Service;

@Service
public class FileDownloadSection  {
//	private static Logger logger = LoggerFactory.getLogger(FileDownloadSection.class);
//	private static ExecutorService fixedUpStreamThreadPool;
//	public static AtomicInteger threadJobSize = new AtomicInteger(0);
//	@Autowired
//	SendPackService sendPackService;
//	@Autowired
//	SectionUtils sectionUtils;
//
//	@Override
//	public void update(String pkgId, PkgStatus status) {
//		// update pkg status here
//
//	}
//	public FileDownloadSection() {
//		synchronized (logger){
//			if(fixedUpStreamThreadPool == null){
//				fixedUpStreamThreadPool =  Executors.newFixedThreadPool(threadNumber,
//						new FileDownloadSection.DepThreadFactory("FileDownloadSection"));
//			}
//		}
//	}
//
//	@Override
//	public SectionResult doThreadAct(final String packageId, String jobId, SectionNode sectionNode, BigDecimal totalSectionNumber, ConcurrentHashMap<String, Object> map) throws Exception {
//		logger.info("[{}] job has [{}],package id [{}] in method ,jobId is [{}]",this.getClass(),threadJobSize.incrementAndGet(),packageId,jobId);
//		fixedUpStreamThreadPool.submit(new Runnable() {
//
//			public void run() {
//				try {
//					logger.info("[{}] , package id [{}] running",this.getClass(),packageId);
//					sectionUtils.insertSectionStepRecorders(map, new BigDecimal(0), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
//
//					update("pkgid", PkgStatus.COMPRSS_BEFORE);
//					// compress pkg here
//
//					sendPackService.downPackMain(map);
//
//					update("pkgid", PkgStatus.COMPRESS_AFTER);
////					return SectionResult(true,map);
//					sectionUtils.insertSectionStepRecorders(map, new BigDecimal(1), this.getClass() + "", jobId, totalSectionNumber,new BigDecimal(0));
//					SectionNode nextSectionNode = sectionNode.getNext();
//					if(nextSectionNode!= null){
//						nextSectionNode.getCurrent().doThreadAct(packageId,jobId,nextSectionNode,totalSectionNumber,map);
//					}
//				}catch (Exception e) {
//					logger.error("[{}] happend error",this.getClass(), e);
//				} finally {
//					logger.info("[{}] job has [{}]",this.getClass(),threadJobSize.decrementAndGet());
//					logger.info("[{}] , package id [{}] run end ",this.getClass(),packageId);
//				}
//			}});
//		return null;
//	}
//	@Override
//	public SectionResult doAct(ConcurrentHashMap<String, Object> map) throws Exception {
//		update("pkgid", PkgStatus.COMPRSS_BEFORE);
//		// compress pkg here
//
//		sendPackService.downPackMain(map);
//
//		update("pkgid", PkgStatus.COMPRESS_AFTER);
//		return SectionResult(true,map);
//	}
//
//	private SectionResult SectionResult(boolean b, ConcurrentHashMap<String, Object> result) {
//		// TODO Auto-generated method stub
//		SectionResult sectionResult = new SectionResult(b);
//		sectionResult.setMap(result);
//		return sectionResult;
//	}

}
