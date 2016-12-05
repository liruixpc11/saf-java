package lab.cadl.analysis.behavior.saf.demo;

public class BatchProcessor extends DemoProcessor {
    public static void main(String[] args) {
//        new BatchProcessor().run("data/sqlite/httpflows_1077rec.sqlite", "net.app_proto.httpdemo");
//        new BatchProcessor().run("data/sqlite/tcpudpdns_mix_20rec.sqlite", "bscripts/tcppktpair");
//        new BatchProcessor().run("data/sqlite/nsdipaper_casestudy2_data.sqlite", "bscripts/dnskaminsky_2");
        new BatchProcessor().run("data/sqlite/nsdipaper_casestudy1_2sec_data.sqlite", "bscripts/doshussain03");
    }
}
