package com.gucl.storm.bolt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordSpliter extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		super.prepare(stormConf, context);
		System.out.println("【WordSpliter-prepare】");
	}
	public void execute(Tuple input, BasicOutputCollector collector) {
		 String line = input.getString(0);
	        String[] words = line.split(" ");
	        for (String word : words) {
	            word = word.trim();
	            if (StringUtils.isNotBlank(word)) {
	                word = word.toLowerCase();
	                collector.emit(new Values(word));
	            }
	        }
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));

	}

}
