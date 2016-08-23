package com.gucl.storm;

import com.gucl.storm.bolt.WordCounter;
import com.gucl.storm.bolt.WordSpliter;
import com.gucl.storm.spout.WordReader;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class WordCountClusterTopology {

	/**
	 *  启动方法   需要将INPUT_PATH和TIME_OFFSET传入main方法的args。
	 *  示例：       e:\\stormtest 2
	 * @param args
	 * @author gucl
	 * @ApiDocMethod
	 * @ApiCode
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
            System.err.println("Usage: inputPaht timeOffset");
            System.err.println("such as : java -jar WordCount.jar E:\\stormtest  2");
            System.exit(2);
        }
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader(),1);
        builder.setBolt("word-spilter", new WordSpliter(),4).shuffleGrouping("word-reader");
        //builder.setBolt("word-counter", new WordCounter(),5).globalGrouping("word-spilter");
        //builder.setBolt("word-counter", new WordCounter(),5).shuffleGrouping("word-spilter");
        builder.setBolt("word-counter", new WordCounter(),5).fieldsGrouping("word-spilter", new Fields("word"));
        String inputPaht = args[0];
        String timeOffset = args[1];
        Config conf = new Config();
        conf.put("INPUT_PATH", inputPaht);
        conf.put("TIME_OFFSET", timeOffset);
        conf.setDebug(false);
        //LocalCluster cluster = new LocalCluster();
        try {
			StormSubmitter.submitTopology("WordCountCluster", conf, builder.createTopology());
		} catch (AlreadyAliveException | InvalidTopologyException e) {
			System.out.println("启动分布式拓扑出错"+e.getMessage());
			e.printStackTrace();
		}

	}

}
