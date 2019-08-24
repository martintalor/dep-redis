package com.iflytek.dep.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DepApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void contextLoads() throws Exception {
		/*try {
			mvc.perform(MockMvcRequestBuilders.get("/api/service/qxgl/jsgl/getGnmkAll")
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

        long startTime = startTest();
        //数据库递归执行 执行时间：27.337s
//		List<GnmkTree> list = service.GetGnmkAll("0");
        //一次读取，JAVA递归执行时间：13.442s
//		List<GnmkTree> list = service.getGnmkTreeAll("0");


        System.out.println("eeeeeeeessssseeeeee");

        //执行时间：29.181s
//		List<HashMap<String, Object>>  list = swjgService.getSwjgTreeN("00000000000");

//		swjgService.getSwjgTreeN("00000000000");

        endTest("DepApplicationTests",startTime);
    }

    public static long startTest() {

        return  System.currentTimeMillis();
    }

    public static float endTest(String caseName,long startTime) {
        long endTime   = System.currentTimeMillis();
        float excTime   =   (float) (endTime - startTime) / 1000;
        System.out.println("==》》【" + caseName + "】执行时间：" + excTime + "s");
        return excTime;
    }

    private static Logger logger = LoggerFactory.getLogger(DepApplicationTests.class);


    public static void main(String[] args) {


        ArrayList<Float> endTims = new ArrayList<Float>();
        for (int i = 0; i < 3; i++) {
//            testLogger1();
            float endTime = testLogger1();
            endTims.add(endTime);
        }

        System.out.println( endTims.toString());
    }

    private static float testLogger1() {
        long startTime = startTest();
        for (int i = 0; i < 1000000; i++) {
            logger.info("[{}] , [{}], [{}] , [{}]","探测",i,"，当前线程",Thread.currentThread().getName());
        }
        float end1 = endTest("logger.error(\"[{}] , [{}]\",\"探测\",i)",startTime);
        return end1;
    }

    private static float testLogger2() {
        long start2 = startTest();
        for (int i = 0; i < 1000000; i++) {
            logger.info("[{}] + [{}]>>>" + "探测" + i + "当前线程" + Thread.currentThread().getName());
        }
        float end2 = endTest("logger.info(\"[{}] + [{}]>>>\" + \"探测\" + i)",start2);

//        System.out.println( end2 );
        return end2;
    }


}
