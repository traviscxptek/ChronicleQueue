package cxptek.main;

import java.util.ArrayList;
import java.util.List;

public class LoadTest {
    public static void main(String[] args) {
        Long loadTestSize = 100_000_000L;
        List<Long> ids = new ArrayList<>();
        for (long i = 0; i < loadTestSize; i++) {
            ids.add(i);
        }
        //start computing
        long start = System.currentTimeMillis();
//        for (int i = 0; i < loadTestSize; i++) {
//            Long id = ids.get(i);
//            id++;
//        }
        for(Long id: ids){
            id++;
        }
        System.out.printf("Load test took: %dms\n", System.currentTimeMillis() - start);
    }
}