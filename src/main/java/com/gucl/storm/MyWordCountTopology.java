package com.gucl.storm;

import com.gucl.storm.base.BaseFlow;
import com.gucl.storm.bolt.WordCounter;
import com.gucl.storm.bolt.WordSpliter;
import com.gucl.storm.spout.WordReader;

import backtype.storm.tuple.Fields;

public class MyWordCountTopology  extends BaseFlow{

	/**
	 * 启动方法  需将cong.yaml的路径传入args。
	 * 示例：     E:\\StormWordCount\\conf.yaml
	 * @param args
	 * @author gucl
	 * @ApiDocMethod
	 * @ApiCode
	 */
	public static void main(String[] args) {
		MyWordCountTopology flow=new MyWordCountTopology();
		flow.run(args);
	}

	@Override
	public void define() {
		// TODO Auto-generated method stub
		builder.setSpout("word-reader", new WordReader(),1);
        builder.setBolt("word-spilter", new WordSpliter(),4).shuffleGrouping("word-reader");
        //builder.setBolt("word-counter", new WordCounter(),5).globalGrouping("word-spilter");
        //builder.setBolt("word-counter", new WordCounter(),5).shuffleGrouping("word-spilter");
        builder.setBolt("word-counter", new WordCounter(),5).fieldsGrouping("word-spilter", new Fields("word"));
	}

}
