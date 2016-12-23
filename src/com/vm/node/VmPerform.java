package com.vm.node;

import java.util.Map;

import com.vm.xen.XenTools;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class VmPerform {

	public static Node getNode(TreeItem<String> node) {
		String vmName = node.getValue();

		final NumberAxis cpuxAxis = new NumberAxis();
		final NumberAxis cpuyAxis = new NumberAxis();
		cpuxAxis.setLabel("时间轴");

		// xAxis.setForceZeroInRange(false);
		cpuyAxis.setUpperBound(100);
		cpuyAxis.setAutoRanging(false);
		cpuyAxis.setLowerBound(0);
		cpuyAxis.setTickUnit(10);
		final LineChart<Number, Number> cpuChart = new LineChart<Number, Number>(cpuxAxis, cpuyAxis);
		cpuChart.setTitle("CPU使用率(%)");
		cpuChart.setCreateSymbols(false);

		final NumberAxis memoryxAxis = new NumberAxis();
		final NumberAxis memoryyAxis = new NumberAxis();
		memoryxAxis.setLabel("时间轴");

		// xAxis.setForceZeroInRange(false);
		// System.out.println(XenTools.getMemoryOfVMByName(nameVm));
		double memTotal = XenTools.getMemoryOfVM(null,vmName);
		memoryyAxis.setUpperBound(memTotal);
		memoryyAxis.setAutoRanging(false);
		memoryyAxis.setLowerBound(0);
		memoryyAxis.setTickUnit(1);
		final LineChart<Number, Number> memoryChart = new LineChart<Number, Number>(memoryxAxis, memoryyAxis);

		memoryChart.setTitle("内存使用(G)");
		memoryChart.setCreateSymbols(false);

		final NumberAxis netxAxis = new NumberAxis();
		final NumberAxis netyAxis = new NumberAxis();
		netxAxis.setLabel("时间轴");

		// xAxis.setForceZeroInRange(false);
		final LineChart<Number, Number> netChart = new LineChart<Number, Number>(netxAxis, netyAxis);

		netChart.setTitle("网络性能(KB)");
		netChart.setCreateSymbols(false);

		final NumberAxis storagexAxis = new NumberAxis();
		final NumberAxis storageyAxis = new NumberAxis();
		storagexAxis.setLabel("时间轴");

		// xAxis.setForceZeroInRange(false);

		final LineChart<Number, Number> storageChart = new LineChart<Number, Number>(storagexAxis, storageyAxis);

		storageChart.setTitle("磁盘读写(KB)");
		storageChart.setCreateSymbols(false);

		if (XenTools.isRunningByName(vmName)) {
			Map<String, Map<Integer, double[]>> preform = XenTools.getVmPerform(vmName);

			Map<Integer, double[]> cpuMap = preform.get("cpuMap");
			Map<Integer, double[]> memoryMap = preform.get("memoryMap");
			Map<Integer, double[]> vifTMap = preform.get("vifTMap");
			Map<Integer, double[]> vifRMap = preform.get("vifRMap");
			Map<Integer, double[]> vbdWMap = preform.get("vbdWMap");
			Map<Integer, double[]> vbdRMap = preform.get("vbdRMap");

			int cpuTotal = cpuMap.size();

			for (int i = 0; i < cpuTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = cpuMap.get(i);
				series.setName("cpu" + i + "( " + String.format("%.2f", data[0] * 100) + "% )");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, data[len - j - 1] * 100));
				}

				cpuChart.getData().add(series);
			}

			int memoryTotal = memoryMap.size();
			for (int i = 0; i < memoryTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = memoryMap.get(i);
				series.setName("内存使用( " + String.format("%.2f", memTotal - data[0] / 1024 / 1024) + " )M");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, memTotal - (data[len - j - 1]) / 1024 / 1024));
				}
				memoryChart.getData().add(series);
			}

			int vifTTotal = vifTMap.size();
			for (int i = 0; i < vifTTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = vifTMap.get(i);
				series.setName("网络" + i + "上行速率: " + String.format("%.2f", data[0] / 1024) + "KB");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
				}
				netChart.getData().add(series);
			}

			int vifRTotal = vifRMap.size();
			for (int i = 0; i < vifRTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = vifRMap.get(i);
				series.setName("网络" + i + "下行速率: " + String.format("%.2f", data[0] / 1024) + "KB");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
				}
				netChart.getData().add(series);
			}

			int vbdWTotal = vbdWMap.size();
			for (int i = 0; i < vbdWTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = vbdWMap.get(i);
				series.setName("磁盘" + i + "写速率: " + String.format("%.2f", data[0] / 1024) + "KB");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
				}
				storageChart.getData().add(series);
			}
			int vbdRTotal = vbdRMap.size();
			for (int i = 0; i < vbdRTotal; i++) {
				XYChart.Series series = new XYChart.Series();

				double[] data = vbdRMap.get(i);
				series.setName("磁盘" + i + "读速率: " + String.format("%.2f", data[0] / 1024) + "KB");
				int len = data.length;
				for (int j = 0; j < len; j++) {
					series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
				}
				storageChart.getData().add(series);
			}

		}

		final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
			@Override
			public void handle(Event event) {

				if (XenTools.isRunningByName(vmName)) {

					cpuChart.getData().clear();
					memoryChart.getData().clear();
					netChart.getData().clear();
					storageChart.getData().clear();

					Map<String, Map<Integer, double[]>> preform = XenTools.getVmPerform(vmName);

					Map<Integer, double[]> cpuMap = preform.get("cpuMap");
					Map<Integer, double[]> memoryMap = preform.get("memoryMap");
					Map<Integer, double[]> vifTMap = preform.get("vifTMap");
					Map<Integer, double[]> vifRMap = preform.get("vifRMap");
					Map<Integer, double[]> vbdWMap = preform.get("vbdWMap");
					Map<Integer, double[]> vbdRMap = preform.get("vbdRMap");

					int cpuTotal = cpuMap.size();

					for (int i = 0; i < cpuTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = cpuMap.get(i);
						series.setName("cpu" + i + "(" + String.format("%.2f", data[0] * 100) + "%)");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, data[len - j - 1] * 100));
						}

						cpuChart.getData().add(series);
					}

					int memoryTotal = memoryMap.size();
					for (int i = 0; i < memoryTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = memoryMap.get(i);
						series.setName("内存使用(" + String.format("%.2f", memTotal - data[0] / 1024 / 1024) + ")G");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, memTotal - (data[len - j - 1]) / 1024 / 1024));
						}
						memoryChart.getData().add(series);
					}

					int vifTTotal = vifTMap.size();
					for (int i = 0; i < vifTTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = vifTMap.get(i);
						series.setName("网络" + i + "上行速率: " + String.format("%.2f", data[0] / 1024) + "KB");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
						}
						netChart.getData().add(series);
					}

					int vifRTotal = vifRMap.size();
					for (int i = 0; i < vifRTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = vifRMap.get(i);
						series.setName("网络" + i + "下行速率: " + String.format("%.2f", data[0] / 1024) + "KB");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
						}
						netChart.getData().add(series);
					}

					int vbdWTotal = vbdWMap.size();
					for (int i = 0; i < vbdWTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = vbdWMap.get(i);
						series.setName("磁盘" + i + "写速率: " + String.format("%.2f", data[0] / 1024) + "KB");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
						}
						storageChart.getData().add(series);
					}
					int vbdRTotal = vbdRMap.size();
					for (int i = 0; i < vbdRTotal; i++) {
						XYChart.Series series = new XYChart.Series();

						double[] data = vbdRMap.get(i);
						series.setName("磁盘" + i + "读速率: " + String.format("%.2f", data[0] / 1024) + "KB");
						int len = data.length;
						for (int j = 0; j < len; j++) {
							series.getData().add(new XYChart.Data(j, (data[len - j - 1]) / 1024));
						}
						storageChart.getData().add(series);
					}
				}
			}
		}), new KeyFrame(Duration.millis(5000)));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		return new VBox(5, cpuChart, new Separator(), memoryChart, new Separator(), netChart, new Separator(),
				storageChart);
	}
	

}
