package org.vcell.chombo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.Extent;

import com.google.gson.Gson;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.solver.SolverDescription;

public class ChomboMeshValidator {
	private Extent extent;
	private int blockFactor;
	private int dim;

	private static class DomainARInfo {
		double[] ar;
		int[] minNx;

		public DomainARInfo(double ar, int[] minNx) {
			super();
			this.ar = new double[] { ar };
			this.minNx = minNx;
		}

		public DomainARInfo(double[] ar, int[] minNx) {
			super();
			this.ar = ar;
			this.minNx = minNx;
		}
	}

	private static final DomainARInfo[] listARDomainInfo2D = {
			new DomainARInfo(1, new int[] {1,1}),
			new DomainARInfo(1.01587301587301, new int[] {63,64}),
			new DomainARInfo(1.02857142857142, new int[] {35,36}),
			new DomainARInfo(1.03703703703703, new int[] {27,28}),
			new DomainARInfo(1.04166666666666, new int[] {24,25}),
			new DomainARInfo(1.05, new int[] {20,21}),
			new DomainARInfo(1.06666666666666, new int[] {15,16}),
			new DomainARInfo(1.07142857142857, new int[] {14,15}),
			new DomainARInfo(1.08, new int[] {25,27}),
			new DomainARInfo(1.09375, new int[] {32,35}),
			new DomainARInfo(1.11111111111111, new int[] {9,10}),
			new DomainARInfo(1.12, new int[] {25,28}),
			new DomainARInfo(1.125, new int[] {8,9}),
			new DomainARInfo(1.14285714285714, new int[] {7,8}),
			new DomainARInfo(1.16666666666666, new int[] {6,7}),
			new DomainARInfo(1.171875, new int[] {64,75}),
			new DomainARInfo(1.18518518518518, new int[] {27,32}),
			new DomainARInfo(1.19047619047619, new int[] {21,25}),
			new DomainARInfo(1.2, new int[] {5,6}),
			new DomainARInfo(1.24444444444444, new int[] {45,56}),
			new DomainARInfo(1.25, new int[] {4,5}),
			new DomainARInfo(1.26, new int[] {50,63}),
			new DomainARInfo(1.26984126984126, new int[] {63,80}),
			new DomainARInfo(1.28, new int[] {25,32}),
			new DomainARInfo(1.28571428571428, new int[] {7,9}),
			new DomainARInfo(1.29629629629629, new int[] {27,35}),
			new DomainARInfo(1.3125, new int[] {16,21}),
			new DomainARInfo(1.33333333333333, new int[] {3,4}),
			new DomainARInfo(1.33928571428571, new int[] {56,75}),
			new DomainARInfo(1.35, new int[] {20,27}),
			new DomainARInfo(1.37142857142857, new int[] {35,48}),
			new DomainARInfo(1.38888888888888, new int[] {18,25}),
			new DomainARInfo(1.4, new int[] {5,7}),
			new DomainARInfo(1.40625, new int[] {32,45}),
			new DomainARInfo(1.42222222222222, new int[] {45,64}),
			new DomainARInfo(1.42857142857142, new int[] {7,10}),
			new DomainARInfo(1.44, new int[] {25,36}),
			new DomainARInfo(1.45833333333333, new int[] {24,35}),
			new DomainARInfo(1.48148148148148, new int[] {27,40}),
			new DomainARInfo(1.5, new int[] {2,3}),
			new DomainARInfo(1.52380952380952, new int[] {21,32}),
			new DomainARInfo(1.54285714285714, new int[] {35,54}),
			new DomainARInfo(1.55555555555555, new int[] {9,14}),
			new DomainARInfo(1.5625, new int[] {16,25}),
			new DomainARInfo(1.575, new int[] {40,63}),
			new DomainARInfo(1.58730158730158, new int[] {63,100}),
			new DomainARInfo(1.6, new int[] {5,8}),
			new DomainARInfo(1.60714285714285, new int[] {28,45}),
			new DomainARInfo(1.640625, new int[] {64,105}),
			new DomainARInfo(1.66666666666666, new int[] {3,5}),
			new DomainARInfo(1.68, new int[] {25,42}),
			new DomainARInfo(1.6875, new int[] {16,27}),
			new DomainARInfo(1.71428571428571, new int[] {7,12}),
			new DomainARInfo(1.75, new int[] {4,7}),
			new DomainARInfo(1.77777777777777, new int[] {9,16}),
			new DomainARInfo(1.78571428571428, new int[] {14,25}),
			new DomainARInfo(1.8, new int[] {5,9}),
			new DomainARInfo(1.82857142857142, new int[] {35,64}),
			new DomainARInfo(1.85185185185185, new int[] {27,50}),
			new DomainARInfo(1.86666666666666, new int[] {15,28}),
			new DomainARInfo(1.875, new int[] {8,15}),
			new DomainARInfo(1.9047619047619, new int[] {21,40}),
			new DomainARInfo(1.92, new int[] {25,48}),
			new DomainARInfo(1.92857142857142, new int[] {14,27}),
			new DomainARInfo(1.94444444444444, new int[] {18,35}),
			new DomainARInfo(1.96875, new int[] {32,63}),
			new DomainARInfo(2, new int[] {1,2}),
			new DomainARInfo(2.05714285714285, new int[] {35,72}),
			new DomainARInfo(2.07407407407407, new int[] {27,56}),
			new DomainARInfo(2.08333333333333, new int[] {12,25}),
			new DomainARInfo(2.1, new int[] {10,21}),
			new DomainARInfo(2.109375, new int[] {64,135}),
			new DomainARInfo(2.13333333333333, new int[] {15,32}),
			new DomainARInfo(2.14285714285714, new int[] {7,15}),
			new DomainARInfo(2.16, new int[] {25,54}),
			new DomainARInfo(2.1875, new int[] {16,35}),
			new DomainARInfo(2.22222222222222, new int[] {9,20}),
			new DomainARInfo(2.24, new int[] {25,56}),
			new DomainARInfo(2.25, new int[] {4,9}),
			new DomainARInfo(2.28571428571428, new int[] {7,16}),
			new DomainARInfo(2.33333333333333, new int[] {3,7}),
			new DomainARInfo(2.34375, new int[] {32,75}),
			new DomainARInfo(2.37037037037037, new int[] {27,64}),
			new DomainARInfo(2.38095238095238, new int[] {21,50}),
			new DomainARInfo(2.4, new int[] {5,12}),
			new DomainARInfo(2.41071428571428, new int[] {56,135}),
			new DomainARInfo(2.48888888888888, new int[] {45,112}),
			new DomainARInfo(2.5, new int[] {2,5}),
			new DomainARInfo(2.52, new int[] {25,63}),
			new DomainARInfo(2.53968253968253, new int[] {63,160}),
			new DomainARInfo(2.56, new int[] {25,64}),
			new DomainARInfo(2.57142857142857, new int[] {7,18}),
			new DomainARInfo(2.59259259259259, new int[] {27,70}),
			new DomainARInfo(2.625, new int[] {8,21}),
			new DomainARInfo(2.66666666666666, new int[] {3,8}),
			new DomainARInfo(2.67857142857142, new int[] {28,75}),
			new DomainARInfo(2.7, new int[] {10,27}),
			new DomainARInfo(2.734375, new int[] {64,175}),
			new DomainARInfo(2.74285714285714, new int[] {35,96}),
			new DomainARInfo(2.77777777777777, new int[] {9,25}),
			new DomainARInfo(2.8, new int[] {5,14}),
			new DomainARInfo(2.8125, new int[] {16,45}),
			new DomainARInfo(2.85714285714285, new int[] {7,20}),
			new DomainARInfo(2.88, new int[] {25,72}),
			new DomainARInfo(2.91666666666666, new int[] {12,35}),
			new DomainARInfo(2.953125, new int[] {64,189}),
			new DomainARInfo(2.96296296296296, new int[] {27,80}),
			new DomainARInfo(3, new int[] {1,3}),
			new DomainARInfo(3.04761904761904, new int[] {21,64}),
			new DomainARInfo(3.08571428571428, new int[] {35,108}),
			new DomainARInfo(3.11111111111111, new int[] {9,28}),
			new DomainARInfo(3.125, new int[] {8,25}),
			new DomainARInfo(3.15, new int[] {20,63}),
			new DomainARInfo(3.17460317460317, new int[] {63,200}),
			new DomainARInfo(3.2, new int[] {5,16}),
			new DomainARInfo(3.21428571428571, new int[] {14,45}),
			new DomainARInfo(3.24074074074074, new int[] {54,175}),
			new DomainARInfo(3.28125, new int[] {32,105}),
			new DomainARInfo(3.33333333333333, new int[] {3,10}),
			new DomainARInfo(3.36, new int[] {25,84}),
			new DomainARInfo(3.375, new int[] {8,27}),
			new DomainARInfo(3.42857142857142, new int[] {7,24}),
			new DomainARInfo(3.5, new int[] {2,7}),
			new DomainARInfo(3.515625, new int[] {64,225}),
			new DomainARInfo(3.55555555555555, new int[] {9,32}),
			new DomainARInfo(3.57142857142857, new int[] {7,25}),
			new DomainARInfo(3.6, new int[] {5,18}),
			new DomainARInfo(3.64583333333333, new int[] {48,175}),
			new DomainARInfo(3.7037037037037, new int[] {27,100}),
			new DomainARInfo(3.73333333333333, new int[] {15,56}),
			new DomainARInfo(3.75, new int[] {4,15}),
			new DomainARInfo(3.78, new int[] {50,189}),
			new DomainARInfo(3.8095238095238, new int[] {21,80}),
			new DomainARInfo(3.84, new int[] {25,96}),
			new DomainARInfo(3.85714285714285, new int[] {7,27}),
			new DomainARInfo(3.88888888888888, new int[] {9,35}),
			new DomainARInfo(3.9375, new int[] {16,63}),
			new DomainARInfo(4, new int[] {1,4}),
			new DomainARInfo(4.01785714285714, new int[] {56,225}),
			new DomainARInfo(4.11428571428571, new int[] {35,144}),
			new DomainARInfo(4.14814814814814, new int[] {27,112}),
			new DomainARInfo(4.16666666666666, new int[] {6,25}),
			new DomainARInfo(4.2, new int[] {5,21}),
			new DomainARInfo(4.21875, new int[] {32,135}),
			new DomainARInfo(4.26666666666666, new int[] {15,64}),
			new DomainARInfo(4.28571428571428, new int[] {7,30}),
			new DomainARInfo(4.32, new int[] {25,108}),
			new DomainARInfo(4.375, new int[] {8,35}),
			new DomainARInfo(4.44444444444444, new int[] {9,40}),
			new DomainARInfo(4.48, new int[] {25,112}),
			new DomainARInfo(4.5, new int[] {2,9}),
			new DomainARInfo(4.57142857142857, new int[] {7,32}),
			new DomainARInfo(4.66666666666666, new int[] {3,14}),
			new DomainARInfo(4.6875, new int[] {16,75}),
			new DomainARInfo(4.725, new int[] {40,189}),
			new DomainARInfo(4.76190476190476, new int[] {21,100}),
			new DomainARInfo(4.8, new int[] {5,24}),
			new DomainARInfo(4.82142857142857, new int[] {28,135}),
			new DomainARInfo(4.86111111111111, new int[] {36,175}),
			new DomainARInfo(4.921875, new int[] {64,315}),
			new DomainARInfo(4.97777777777777, new int[] {45,224}),
			new DomainARInfo(5, new int[] {1,5}),
			new DomainARInfo(5.04, new int[] {25,126}),
			new DomainARInfo(5.07936507936507, new int[] {63,320}),
			new DomainARInfo(5.14285714285714, new int[] {7,36}),
			new DomainARInfo(5.18518518518518, new int[] {27,140}),
			new DomainARInfo(5.25, new int[] {4,21}),
			new DomainARInfo(5.33333333333333, new int[] {3,16}),
			new DomainARInfo(5.35714285714285, new int[] {14,75}),
			new DomainARInfo(5.4, new int[] {5,27}),
			new DomainARInfo(5.46875, new int[] {32,175}),
			new DomainARInfo(5.48571428571428, new int[] {35,192}),
			new DomainARInfo(5.55555555555555, new int[] {9,50}),
			new DomainARInfo(5.6, new int[] {5,28}),
			new DomainARInfo(5.625, new int[] {8,45}),
			new DomainARInfo(5.71428571428571, new int[] {7,40}),
			new DomainARInfo(5.76, new int[] {25,144}),
			new DomainARInfo(5.83333333333333, new int[] {6,35}),
			new DomainARInfo(5.90625, new int[] {32,189}),
			new DomainARInfo(5.92592592592592, new int[] {27,160}),
			new DomainARInfo(6, new int[] {1,6}),
			new DomainARInfo(6.17142857142857, new int[] {35,216}),
			new DomainARInfo(6.22222222222222, new int[] {9,56}),
			new DomainARInfo(6.25, new int[] {4,25}),
			new DomainARInfo(6.3, new int[] {10,63}),
			new DomainARInfo(6.34920634920634, new int[] {63,400}),
			new DomainARInfo(6.4, new int[] {5,32}),
			new DomainARInfo(6.42857142857142, new int[] {7,45}),
			new DomainARInfo(6.48148148148148, new int[] {27,175}),
			new DomainARInfo(6.5625, new int[] {16,105}),
			new DomainARInfo(6.66666666666666, new int[] {3,20}),
			new DomainARInfo(6.72, new int[] {25,168}),
			new DomainARInfo(6.75, new int[] {4,27}),
			new DomainARInfo(6.85714285714285, new int[] {7,48}),
			new DomainARInfo(7, new int[] {1,7}),
			new DomainARInfo(7.03125, new int[] {32,225}),
			new DomainARInfo(7.11111111111111, new int[] {9,64}),
			new DomainARInfo(7.14285714285714, new int[] {7,50}),
			new DomainARInfo(7.2, new int[] {5,36}),
			new DomainARInfo(7.29166666666666, new int[] {24,175}),
			new DomainARInfo(7.4074074074074, new int[] {27,200}),
			new DomainARInfo(7.46666666666666, new int[] {15,112}),
			new DomainARInfo(7.5, new int[] {2,15}),
			new DomainARInfo(7.56, new int[] {25,189}),
			new DomainARInfo(7.61904761904761, new int[] {21,160}),
			new DomainARInfo(7.68, new int[] {25,192}),
			new DomainARInfo(7.71428571428571, new int[] {7,54}),
			new DomainARInfo(7.77777777777777, new int[] {9,70}),
			new DomainARInfo(7.875, new int[] {8,63}),
			new DomainARInfo(8, new int[] {1,8}),
			new DomainARInfo(8.03571428571428, new int[] {28,225}),
			new DomainARInfo(8.203125, new int[] {64,525}),
			new DomainARInfo(8.22857142857142, new int[] {35,288}),
			new DomainARInfo(8.29629629629629, new int[] {27,224}),
			new DomainARInfo(8.33333333333333, new int[] {3,25}),
			new DomainARInfo(8.4, new int[] {5,42}),
			new DomainARInfo(8.4375, new int[] {16,135}),
			new DomainARInfo(8.57142857142857, new int[] {7,60}),
			new DomainARInfo(8.64, new int[] {25,216}),
			new DomainARInfo(8.75, new int[] {4,35}),
			new DomainARInfo(8.88888888888889, new int[] {9,80}),
			new DomainARInfo(8.96, new int[] {25,224}),
			new DomainARInfo(9, new int[] {1,9}),
			new DomainARInfo(9.14285714285714, new int[] {7,64}),
			new DomainARInfo(9.33333333333333, new int[] {3,28}),
			new DomainARInfo(9.375, new int[] {8,75}),
			new DomainARInfo(9.45, new int[] {20,189}),
			new DomainARInfo(9.52380952380952, new int[] {21,200}),
			new DomainARInfo(9.6, new int[] {5,48}),
			new DomainARInfo(9.64285714285714, new int[] {14,135}),
			new DomainARInfo(9.72222222222222, new int[] {18,175}),
			new DomainARInfo(9.84375, new int[] {32,315}),
			new DomainARInfo(9.95555555555555, new int[] {45,448}),
			new DomainARInfo(10, new int[] {1,10}),
			new DomainARInfo(10.08, new int[] {25,252}),
			new DomainARInfo(10.2857142857142, new int[] {7,72}),
			new DomainARInfo(10.3703703703703, new int[] {27,280}),
			new DomainARInfo(10.5, new int[] {2,21}),
			new DomainARInfo(10.546875, new int[] {64,675}),
			new DomainARInfo(10.6666666666666, new int[] {3,32}),
			new DomainARInfo(10.7142857142857, new int[] {7,75}),
			new DomainARInfo(10.8, new int[] {5,54}),
			new DomainARInfo(10.9375, new int[] {16,175}),
			new DomainARInfo(11.1111111111111, new int[] {9,100}),
			new DomainARInfo(11.2, new int[] {5,56}),
			new DomainARInfo(11.25, new int[] {4,45}),
			new DomainARInfo(11.4285714285714, new int[] {7,80}),
			new DomainARInfo(11.52, new int[] {25,288}),
			new DomainARInfo(11.6666666666666, new int[] {3,35}),
			new DomainARInfo(11.8125, new int[] {16,189}),
			new DomainARInfo(11.8518518518518, new int[] {27,320}),
			new DomainARInfo(12, new int[] {1,12}),
			new DomainARInfo(12.0535714285714, new int[] {56,675}),
			new DomainARInfo(12.3428571428571, new int[] {35,432}),
			new DomainARInfo(12.4444444444444, new int[] {9,112}),
			new DomainARInfo(12.5, new int[] {2,25}),
			new DomainARInfo(12.6, new int[] {5,63}),
			new DomainARInfo(12.6984126984126, new int[] {63,800}),
			new DomainARInfo(12.8, new int[] {5,64}),
			new DomainARInfo(12.8571428571428, new int[] {7,90}),
			new DomainARInfo(12.9629629629629, new int[] {27,350}),
			new DomainARInfo(13.125, new int[] {8,105}),
			new DomainARInfo(13.3333333333333, new int[] {3,40}),
			new DomainARInfo(13.44, new int[] {25,336}),
			new DomainARInfo(13.5, new int[] {2,27}),
			new DomainARInfo(13.7142857142857, new int[] {7,96}),
			new DomainARInfo(14, new int[] {1,14}),
			new DomainARInfo(14.0625, new int[] {16,225}),
			new DomainARInfo(14.2857142857142, new int[] {7,100}),
			new DomainARInfo(14.4, new int[] {5,72}),
			new DomainARInfo(14.5833333333333, new int[] {12,175}),
			new DomainARInfo(14.765625, new int[] {64,945}),
			new DomainARInfo(14.8148148148148, new int[] {27,400}),
			new DomainARInfo(14.9333333333333, new int[] {15,224}),
			new DomainARInfo(15, new int[] {1,15}),
			new DomainARInfo(15.12, new int[] {25,378}),
			new DomainARInfo(15.2380952380952, new int[] {21,320}),
			new DomainARInfo(15.4285714285714, new int[] {7,108}),
			new DomainARInfo(15.5555555555555, new int[] {9,140}),
			new DomainARInfo(15.75, new int[] {4,63}),
			new DomainARInfo(16, new int[] {1,16}),
			new DomainARInfo(16.0714285714285, new int[] {14,225}),
			new DomainARInfo(16.40625, new int[] {32,525}),
			new DomainARInfo(16.4571428571428, new int[] {35,576}),
			new DomainARInfo(16.5925925925925, new int[] {27,448}),
			new DomainARInfo(16.6666666666666, new int[] {3,50}),
			new DomainARInfo(16.8, new int[] {5,84}),
			new DomainARInfo(16.875, new int[] {8,135}),
			new DomainARInfo(17.1428571428571, new int[] {7,120}),
			new DomainARInfo(17.28, new int[] {25,432}),
			new DomainARInfo(17.5, new int[] {2,35}),
			new DomainARInfo(17.7777777777777, new int[] {9,160}),
			new DomainARInfo(17.92, new int[] {25,448}),
			new DomainARInfo(18, new int[] {1,18}),
			new DomainARInfo(18.6666666666666, new int[] {3,56}),
			new DomainARInfo(18.75, new int[] {4,75}),
			new DomainARInfo(18.9, new int[] {10,189}),
			new DomainARInfo(19.047619047619, new int[] {21,400}),
			new DomainARInfo(19.2, new int[] {5,96}),
			new DomainARInfo(19.2857142857142, new int[] {7,135}),
			new DomainARInfo(19.4444444444444, new int[] {9,175}),
			new DomainARInfo(19.6875, new int[] {16,315}),
			new DomainARInfo(20, new int[] {1,20}),
			new DomainARInfo(20.16, new int[] {25,504}),
			new DomainARInfo(20.5714285714285, new int[] {7,144}),
			new DomainARInfo(20.7407407407407, new int[] {27,560}),
			new DomainARInfo(21, new int[] {1,21}),
			new DomainARInfo(21.09375, new int[] {32,675}),
			new DomainARInfo(21.3333333333333, new int[] {3,64}),
			new DomainARInfo(21.4285714285714, new int[] {7,150}),
			new DomainARInfo(21.6, new int[] {5,108}),
			new DomainARInfo(21.875, new int[] {8,175}),
			new DomainARInfo(22.2222222222222, new int[] {9,200}),
			new DomainARInfo(22.4, new int[] {5,112}),
			new DomainARInfo(22.5, new int[] {2,45}),
			new DomainARInfo(22.8571428571428, new int[] {7,160}),
			new DomainARInfo(23.04, new int[] {25,576}),
			new DomainARInfo(23.3333333333333, new int[] {3,70}),
			new DomainARInfo(23.625, new int[] {8,189}),
			new DomainARInfo(24, new int[] {1,24}),
			new DomainARInfo(24.1071428571428, new int[] {28,675}),
			new DomainARInfo(24.609375, new int[] {64,1575}),
			new DomainARInfo(24.6857142857142, new int[] {35,864}),
			new DomainARInfo(24.8888888888888, new int[] {9,224}),
			new DomainARInfo(25, new int[] {1,25}),
			new DomainARInfo(25.2, new int[] {5,126}),
			new DomainARInfo(25.3968253968253, new int[] {63,1600}),
			new DomainARInfo(25.7142857142857, new int[] {7,180}),
			new DomainARInfo(25.9259259259259, new int[] {27,700}),
			new DomainARInfo(26.25, new int[] {4,105}),
			new DomainARInfo(26.6666666666666, new int[] {3,80}),
			new DomainARInfo(26.88, new int[] {25,672}),
			new DomainARInfo(27, new int[] {1,27}),
			new DomainARInfo(27.4285714285714, new int[] {7,192}),
			new DomainARInfo(28, new int[] {1,28}),
			new DomainARInfo(28.125, new int[] {8,225}),
			new DomainARInfo(28.5714285714285, new int[] {7,200}),
			new DomainARInfo(28.8, new int[] {5,144}),
			new DomainARInfo(29.1666666666666, new int[] {6,175}),
			new DomainARInfo(29.53125, new int[] {32,945}),
			new DomainARInfo(29.6296296296296, new int[] {27,800}),
			new DomainARInfo(29.8666666666666, new int[] {15,448}),
			new DomainARInfo(30, new int[] {1,30}),
			new DomainARInfo(30.24, new int[] {25,756}),
			new DomainARInfo(30.8571428571428, new int[] {7,216}),
			new DomainARInfo(31.1111111111111, new int[] {9,280}),
			new DomainARInfo(31.5, new int[] {2,63}),
			new DomainARInfo(32, new int[] {1,32}),
			new DomainARInfo(32.1428571428571, new int[] {7,225}),
			new DomainARInfo(32.8125, new int[] {16,525}),
			new DomainARInfo(33.3333333333333, new int[] {3,100}),
			new DomainARInfo(33.6, new int[] {5,168}),
			new DomainARInfo(33.75, new int[] {4,135}),
			new DomainARInfo(34.2857142857142, new int[] {7,240}),
			new DomainARInfo(34.56, new int[] {25,864}),
			new DomainARInfo(35, new int[] {1,35}),
			new DomainARInfo(35.5555555555555, new int[] {9,320}),
			new DomainARInfo(36, new int[] {1,36}),
			new DomainARInfo(37.3333333333333, new int[] {3,112}),
			new DomainARInfo(37.5, new int[] {2,75}),
			new DomainARInfo(37.8, new int[] {5,189}),
			new DomainARInfo(38.095238095238, new int[] {21,800}),
			new DomainARInfo(38.4, new int[] {5,192}),
			new DomainARInfo(38.5714285714285, new int[] {7,270}),
			new DomainARInfo(38.8888888888888, new int[] {9,350}),
			new DomainARInfo(39.375, new int[] {8,315}),
			new DomainARInfo(40, new int[] {1,40}),
			new DomainARInfo(40.32, new int[] {25,1008}),
			new DomainARInfo(41.1428571428571, new int[] {7,288}),
			new DomainARInfo(41.4814814814814, new int[] {27,1120}),
			new DomainARInfo(42, new int[] {1,42}),
			new DomainARInfo(42.1875, new int[] {16,675}),
			new DomainARInfo(42.8571428571428, new int[] {7,300}),
			new DomainARInfo(43.2, new int[] {5,216}),
			new DomainARInfo(43.75, new int[] {4,175}),
			new DomainARInfo(44.4444444444444, new int[] {9,400}),
			new DomainARInfo(44.8, new int[] {5,224}),
			new DomainARInfo(45, new int[] {1,45}),
			new DomainARInfo(45.7142857142857, new int[] {7,320}),
			new DomainARInfo(46.6666666666666, new int[] {3,140}),
			new DomainARInfo(47.25, new int[] {4,189}),
			new DomainARInfo(48, new int[] {1,48}),
			new DomainARInfo(48.2142857142857, new int[] {14,675}),
			new DomainARInfo(49.21875, new int[] {32,1575}),
			new DomainARInfo(49.3714285714285, new int[] {35,1728}),
			new DomainARInfo(49.7777777777777, new int[] {9,448}),
			new DomainARInfo(50, new int[] {1,50}),
			new DomainARInfo(50.4, new int[] {5,252}),
			new DomainARInfo(51.4285714285714, new int[] {7,360}),
			new DomainARInfo(51.8518518518518, new int[] {27,1400}),
			new DomainARInfo(52.5, new int[] {2,105}),
			new DomainARInfo(53.3333333333333, new int[] {3,160}),
			new DomainARInfo(53.76, new int[] {25,1344}),
			new DomainARInfo(54, new int[] {1,54}),
			new DomainARInfo(56, new int[] {1,56}),
			new DomainARInfo(56.25, new int[] {4,225}),
			new DomainARInfo(57.1428571428571, new int[] {7,400}),
			new DomainARInfo(57.6, new int[] {5,288}),
			new DomainARInfo(58.3333333333333, new int[] {3,175}),
			new DomainARInfo(59.0625, new int[] {16,945}),
			new DomainARInfo(59.2592592592592, new int[] {27,1600}),
			new DomainARInfo(60, new int[] {1,60}),
			new DomainARInfo(60.48, new int[] {25,1512}),
			new DomainARInfo(61.7142857142857, new int[] {7,432}),
			new DomainARInfo(62.2222222222222, new int[] {9,560}),
			new DomainARInfo(63, new int[] {1,63}),
			new DomainARInfo(64, new int[] {1,64}),
			new DomainARInfo(64.2857142857142, new int[] {7,450}),
			new DomainARInfo(65.625, new int[] {8,525}),
			new DomainARInfo(66.6666666666666, new int[] {3,200}),
			new DomainARInfo(67.2, new int[] {5,336}),
			new DomainARInfo(67.5, new int[] {2,135}),
			new DomainARInfo(68.5714285714285, new int[] {7,480}),
			new DomainARInfo(69.12, new int[] {25,1728}),
			new DomainARInfo(70, new int[] {1,70}),
			new DomainARInfo(72, new int[] {1,72}),
			new DomainARInfo(73.828125, new int[] {64,4725}),
			new DomainARInfo(74.6666666666666, new int[] {3,224}),
			new DomainARInfo(75, new int[] {1,75}),
			new DomainARInfo(75.6, new int[] {5,378}),
			new DomainARInfo(76.1904761904761, new int[] {21,1600}),
			new DomainARInfo(77.1428571428571, new int[] {7,540}),
			new DomainARInfo(77.7777777777777, new int[] {9,700}),
			new DomainARInfo(78.75, new int[] {4,315}),
			new DomainARInfo(80, new int[] {1,80}),
			new DomainARInfo(80.64, new int[] {25,2016}),
			new DomainARInfo(82.2857142857142, new int[] {7,576}),
			new DomainARInfo(82.9629629629629, new int[] {27,2240}),
			new DomainARInfo(84, new int[] {1,84}),
			new DomainARInfo(84.375, new int[] {8,675}),
			new DomainARInfo(85.7142857142857, new int[] {7,600}),
			new DomainARInfo(86.4, new int[] {5,432}),
			new DomainARInfo(87.5, new int[] {2,175}),
			new DomainARInfo(88.8888888888888, new int[] {9,800}),
			new DomainARInfo(89.6, new int[] {5,448}),
			new DomainARInfo(90, new int[] {1,90}),
			new DomainARInfo(93.3333333333333, new int[] {3,280}),
			new DomainARInfo(94.5, new int[] {2,189}),
			new DomainARInfo(96, new int[] {1,96}),
			new DomainARInfo(96.4285714285714, new int[] {7,675}),
			new DomainARInfo(98.4375, new int[] {16,1575}),
			new DomainARInfo(100, new int[] {1,100}),
			new DomainARInfo(100.8, new int[] {5,504}),
			new DomainARInfo(102.857142857142, new int[] {7,720}),
			new DomainARInfo(103.703703703703, new int[] {27,2800}),
			new DomainARInfo(105, new int[] {1,105}),
			new DomainARInfo(106.666666666666, new int[] {3,320}),
			new DomainARInfo(108, new int[] {1,108}),
			new DomainARInfo(112, new int[] {1,112}),
			new DomainARInfo(112.5, new int[] {2,225}),
			new DomainARInfo(114.285714285714, new int[] {7,800}),
			new DomainARInfo(115.2, new int[] {5,576}),
			new DomainARInfo(116.666666666666, new int[] {3,350}),
			new DomainARInfo(118.125, new int[] {8,945}),
			new DomainARInfo(120, new int[] {1,120}),
			new DomainARInfo(120.96, new int[] {25,3024}),
			new DomainARInfo(123.428571428571, new int[] {7,864}),
			new DomainARInfo(124.444444444444, new int[] {9,1120}),
			new DomainARInfo(126, new int[] {1,126}),
			new DomainARInfo(128.571428571428, new int[] {7,900}),
			new DomainARInfo(131.25, new int[] {4,525}),
			new DomainARInfo(133.333333333333, new int[] {3,400}),
			new DomainARInfo(134.4, new int[] {5,672}),
			new DomainARInfo(135, new int[] {1,135}),
			new DomainARInfo(137.142857142857, new int[] {7,960}),
			new DomainARInfo(140, new int[] {1,140}),
			new DomainARInfo(144, new int[] {1,144}),
			new DomainARInfo(147.65625, new int[] {32,4725}),
			new DomainARInfo(149.333333333333, new int[] {3,448}),
			new DomainARInfo(150, new int[] {1,150}),
			new DomainARInfo(151.2, new int[] {5,756}),
			new DomainARInfo(154.285714285714, new int[] {7,1080}),
			new DomainARInfo(155.555555555555, new int[] {9,1400}),
			new DomainARInfo(157.5, new int[] {2,315}),
			new DomainARInfo(160, new int[] {1,160}),
			new DomainARInfo(161.28, new int[] {25,4032}),
			new DomainARInfo(168, new int[] {1,168}),
			new DomainARInfo(168.75, new int[] {4,675}),
			new DomainARInfo(171.428571428571, new int[] {7,1200}),
			new DomainARInfo(172.8, new int[] {5,864}),
			new DomainARInfo(175, new int[] {1,175}),
			new DomainARInfo(177.777777777777, new int[] {9,1600}),
			new DomainARInfo(180, new int[] {1,180}),
			new DomainARInfo(186.666666666666, new int[] {3,560}),
			new DomainARInfo(189, new int[] {1,189}),
			new DomainARInfo(192, new int[] {1,192}),
			new DomainARInfo(192.857142857142, new int[] {7,1350}),
			new DomainARInfo(196.875, new int[] {8,1575}),
			new DomainARInfo(200, new int[] {1,200}),
			new DomainARInfo(201.6, new int[] {5,1008}),
			new DomainARInfo(205.714285714285, new int[] {7,1440}),
			new DomainARInfo(207.407407407407, new int[] {27,5600}),
			new DomainARInfo(210, new int[] {1,210}),
			new DomainARInfo(216, new int[] {1,216}),
			new DomainARInfo(224, new int[] {1,224}),
			new DomainARInfo(225, new int[] {1,225}),
			new DomainARInfo(228.571428571428, new int[] {7,1600}),
			new DomainARInfo(233.333333333333, new int[] {3,700}),
			new DomainARInfo(236.25, new int[] {4,945}),
			new DomainARInfo(240, new int[] {1,240}),
			new DomainARInfo(241.92, new int[] {25,6048}),
			new DomainARInfo(246.857142857142, new int[] {7,1728}),
			new DomainARInfo(248.888888888888, new int[] {9,2240}),
			new DomainARInfo(252, new int[] {1,252}),
			new DomainARInfo(257.142857142857, new int[] {7,1800}),
			new DomainARInfo(262.5, new int[] {2,525}),
			new DomainARInfo(266.666666666666, new int[] {3,800}),
			new DomainARInfo(268.8, new int[] {5,1344}),
			new DomainARInfo(270, new int[] {1,270}),
			new DomainARInfo(280, new int[] {1,280}),
			new DomainARInfo(288, new int[] {1,288}),
			new DomainARInfo(295.3125, new int[] {16,4725}),
			new DomainARInfo(300, new int[] {1,300}),
			new DomainARInfo(302.4, new int[] {5,1512}),
			new DomainARInfo(308.571428571428, new int[] {7,2160}),
			new DomainARInfo(311.111111111111, new int[] {9,2800}),
			new DomainARInfo(315, new int[] {1,315}),
			new DomainARInfo(320, new int[] {1,320}),
			new DomainARInfo(336, new int[] {1,336}),
			new DomainARInfo(337.5, new int[] {2,675}),
			new DomainARInfo(342.857142857142, new int[] {7,2400}),
			new DomainARInfo(345.6, new int[] {5,1728}),
			new DomainARInfo(350, new int[] {1,350}),
			new DomainARInfo(360, new int[] {1,360}),
			new DomainARInfo(373.333333333333, new int[] {3,1120}),
			new DomainARInfo(378, new int[] {1,378}),
			new DomainARInfo(385.714285714285, new int[] {7,2700}),
			new DomainARInfo(393.75, new int[] {4,1575}),
			new DomainARInfo(400, new int[] {1,400}),
			new DomainARInfo(403.2, new int[] {5,2016}),
			new DomainARInfo(411.428571428571, new int[] {7,2880}),
			new DomainARInfo(414.814814814814, new int[] {27,11200}),
			new DomainARInfo(420, new int[] {1,420}),
			new DomainARInfo(432, new int[] {1,432}),
			new DomainARInfo(448, new int[] {1,448}),
			new DomainARInfo(450, new int[] {1,450}),
			new DomainARInfo(466.666666666666, new int[] {3,1400}),
			new DomainARInfo(472.5, new int[] {2,945}),
			new DomainARInfo(480, new int[] {1,480}),
			new DomainARInfo(483.84, new int[] {25,12096}),
			new DomainARInfo(504, new int[] {1,504}),
			new DomainARInfo(514.285714285714, new int[] {7,3600}),
			new DomainARInfo(525, new int[] {1,525}),
			new DomainARInfo(533.333333333333, new int[] {3,1600}),
			new DomainARInfo(540, new int[] {1,540}),
			new DomainARInfo(560, new int[] {1,560}),
			new DomainARInfo(576, new int[] {1,576}),
			new DomainARInfo(590.625, new int[] {8,4725}),
			new DomainARInfo(600, new int[] {1,600}),
			new DomainARInfo(604.8, new int[] {5,3024}),
			new DomainARInfo(617.142857142857, new int[] {7,4320}),
			new DomainARInfo(622.222222222222, new int[] {9,5600}),
			new DomainARInfo(630, new int[] {1,630}),
			new DomainARInfo(672, new int[] {1,672}),
			new DomainARInfo(675, new int[] {1,675}),
			new DomainARInfo(685.714285714285, new int[] {7,4800}),
			new DomainARInfo(700, new int[] {1,700}),
			new DomainARInfo(720, new int[] {1,720}),
			new DomainARInfo(746.666666666666, new int[] {3,2240}),
			new DomainARInfo(756, new int[] {1,756}),
			new DomainARInfo(771.428571428571, new int[] {7,5400}),
			new DomainARInfo(787.5, new int[] {2,1575}),
			new DomainARInfo(800, new int[] {1,800}),
			new DomainARInfo(806.4, new int[] {5,4032}),
			new DomainARInfo(840, new int[] {1,840}),
			new DomainARInfo(864, new int[] {1,864}),
			new DomainARInfo(900, new int[] {1,900}),
			new DomainARInfo(933.333333333333, new int[] {3,2800}),
			new DomainARInfo(945, new int[] {1,945}),
			new DomainARInfo(960, new int[] {1,960}),
			new DomainARInfo(1008, new int[] {1,1008}),
			new DomainARInfo(1028.57142857142, new int[] {7,7200}),
			new DomainARInfo(1050, new int[] {1,1050}),
			new DomainARInfo(1080, new int[] {1,1080}),
			new DomainARInfo(1120, new int[] {1,1120}),
			new DomainARInfo(1181.25, new int[] {4,4725}),
			new DomainARInfo(1200, new int[] {1,1200}),
			new DomainARInfo(1209.6, new int[] {5,6048}),
			new DomainARInfo(1234.28571428571, new int[] {7,8640}),
			new DomainARInfo(1244.44444444444, new int[] {9,11200}),
			new DomainARInfo(1260, new int[] {1,1260}),
			new DomainARInfo(1344, new int[] {1,1344}),
			new DomainARInfo(1350, new int[] {1,1350}),
			new DomainARInfo(1400, new int[] {1,1400}),
			new DomainARInfo(1440, new int[] {1,1440}),
			new DomainARInfo(1512, new int[] {1,1512}),
			new DomainARInfo(1542.85714285714, new int[] {7,10800}),
			new DomainARInfo(1575, new int[] {1,1575}),
			new DomainARInfo(1600, new int[] {1,1600}),
			new DomainARInfo(1680, new int[] {1,1680}),
			new DomainARInfo(1728, new int[] {1,1728}),
			new DomainARInfo(1800, new int[] {1,1800}),
			new DomainARInfo(1866.66666666666, new int[] {3,5600}),
			new DomainARInfo(1890, new int[] {1,1890}),
			new DomainARInfo(2016, new int[] {1,2016}),			
	};

	private static final DomainARInfo[] listARDomainInfo3D = {
			new DomainARInfo(new double[]{1,1}, new int[] {1,1,1}),
			new DomainARInfo(new double[]{1,1.14285714285714}, new int[] {7,7,8}),
			new DomainARInfo(new double[]{1,1.16666666666666}, new int[] {6,6,7}),
			new DomainARInfo(new double[]{1,1.2}, new int[] {5,5,6}),
			new DomainARInfo(new double[]{1,1.25}, new int[] {4,4,5}),
			new DomainARInfo(new double[]{1,1.33333333333333}, new int[] {3,3,4}),
			new DomainARInfo(new double[]{1,1.4}, new int[] {5,5,7}),
			new DomainARInfo(new double[]{1,1.42857142857142}, new int[] {7,7,10}),
			new DomainARInfo(new double[]{1,1.5}, new int[] {2,2,3}),
			new DomainARInfo(new double[]{1,1.6}, new int[] {5,5,8}),
			new DomainARInfo(new double[]{1,1.66666666666666}, new int[] {3,3,5}),
			new DomainARInfo(new double[]{1,1.71428571428571}, new int[] {7,7,12}),
			new DomainARInfo(new double[]{1,1.75}, new int[] {4,4,7}),
			new DomainARInfo(new double[]{1,2}, new int[] {1,1,2}),
			new DomainARInfo(new double[]{1,2.4}, new int[] {5,5,12}),
			new DomainARInfo(new double[]{1,2.5}, new int[] {2,2,5}),
			new DomainARInfo(new double[]{1,2.66666666666666}, new int[] {3,3,8}),
			new DomainARInfo(new double[]{1,3}, new int[] {1,1,3}),
			new DomainARInfo(new double[]{1,3.2}, new int[] {5,5,16}),
			new DomainARInfo(new double[]{1,3.33333333333333}, new int[] {3,3,10}),
			new DomainARInfo(new double[]{1,3.75}, new int[] {4,4,15}),
			new DomainARInfo(new double[]{1,4}, new int[] {1,1,4}),
			new DomainARInfo(new double[]{1,4.8}, new int[] {5,5,24}),
			new DomainARInfo(new double[]{1,5}, new int[] {1,1,5}),
			new DomainARInfo(new double[]{1,5.33333333333333}, new int[] {3,3,16}),
			new DomainARInfo(new double[]{1,6}, new int[] {1,1,6}),
			new DomainARInfo(new double[]{1,6.66666666666666}, new int[] {3,3,20}),
			new DomainARInfo(new double[]{1,7.5}, new int[] {2,2,15}),
			new DomainARInfo(new double[]{1,8}, new int[] {1,1,8}),
			new DomainARInfo(new double[]{1,9.6}, new int[] {5,5,48}),
			new DomainARInfo(new double[]{1,10}, new int[] {1,1,10}),
			new DomainARInfo(new double[]{1,12}, new int[] {1,1,12}),
			new DomainARInfo(new double[]{1,13.3333333333333}, new int[] {3,3,40}),
			new DomainARInfo(new double[]{1,15}, new int[] {1,1,15}),
			new DomainARInfo(new double[]{1,16}, new int[] {1,1,16}),
			new DomainARInfo(new double[]{1,20}, new int[] {1,1,20}),
			new DomainARInfo(new double[]{1,24}, new int[] {1,1,24}),
			new DomainARInfo(new double[]{1,26.6666666666666}, new int[] {3,3,80}),
			new DomainARInfo(new double[]{1,30}, new int[] {1,1,30}),
			new DomainARInfo(new double[]{1,40}, new int[] {1,1,40}),
			new DomainARInfo(new double[]{1,64}, new int[] {1,1,64}),
			new DomainARInfo(new double[]{1,80}, new int[] {1,1,80}),
			new DomainARInfo(new double[]{1,160}, new int[] {1,1,160}),
			new DomainARInfo(new double[]{1,320}, new int[] {1,1,320}),
			new DomainARInfo(new double[]{1.14285714285714,1.14285714285714}, new int[] {7,8,8}),
			new DomainARInfo(new double[]{1.14285714285714,1.42857142857142}, new int[] {7,8,10}),
			new DomainARInfo(new double[]{1.14285714285714,1.71428571428571}, new int[] {7,8,12}),
			new DomainARInfo(new double[]{1.14285714285714,2}, new int[] {7,8,14}),
			new DomainARInfo(new double[]{1.16666666666666,1.16666666666666}, new int[] {6,7,7}),
			new DomainARInfo(new double[]{1.16666666666666,1.33333333333333}, new int[] {6,7,8}),
			new DomainARInfo(new double[]{1.16666666666666,1.66666666666666}, new int[] {6,7,10}),
			new DomainARInfo(new double[]{1.16666666666666,2}, new int[] {6,7,12}),
			new DomainARInfo(new double[]{1.2,1.2}, new int[] {5,6,6}),
			new DomainARInfo(new double[]{1.2,1.4}, new int[] {5,6,7}),
			new DomainARInfo(new double[]{1.2,1.6}, new int[] {5,6,8}),
			new DomainARInfo(new double[]{1.2,2}, new int[] {5,6,10}),
			new DomainARInfo(new double[]{1.2,2.4}, new int[] {5,6,12}),
			new DomainARInfo(new double[]{1.2,3}, new int[] {5,6,15}),
			new DomainARInfo(new double[]{1.2,3.2}, new int[] {5,6,16}),
			new DomainARInfo(new double[]{1.2,4}, new int[] {5,6,20}),
			new DomainARInfo(new double[]{1.2,4.8}, new int[] {5,6,24}),
			new DomainARInfo(new double[]{1.2,6}, new int[] {5,6,30}),
			new DomainARInfo(new double[]{1.2,8}, new int[] {5,6,40}),
			new DomainARInfo(new double[]{1.2,9.6}, new int[] {5,6,48}),
			new DomainARInfo(new double[]{1.2,12}, new int[] {5,6,60}),
			new DomainARInfo(new double[]{1.2,16}, new int[] {5,6,80}),
			new DomainARInfo(new double[]{1.2,24}, new int[] {5,6,120}),
			new DomainARInfo(new double[]{1.25,1.25}, new int[] {4,5,5}),
			new DomainARInfo(new double[]{1.25,1.5}, new int[] {4,5,6}),
			new DomainARInfo(new double[]{1.25,1.75}, new int[] {4,5,7}),
			new DomainARInfo(new double[]{1.25,2}, new int[] {4,5,8}),
			new DomainARInfo(new double[]{1.25,2.5}, new int[] {4,5,10}),
			new DomainARInfo(new double[]{1.25,3}, new int[] {4,5,12}),
			new DomainARInfo(new double[]{1.25,3.75}, new int[] {4,5,15}),
			new DomainARInfo(new double[]{1.25,4}, new int[] {4,5,16}),
			new DomainARInfo(new double[]{1.25,5}, new int[] {4,5,20}),
			new DomainARInfo(new double[]{1.25,6}, new int[] {4,5,24}),
			new DomainARInfo(new double[]{1.25,7.5}, new int[] {4,5,30}),
			new DomainARInfo(new double[]{1.25,10}, new int[] {4,5,40}),
			new DomainARInfo(new double[]{1.25,12}, new int[] {4,5,48}),
			new DomainARInfo(new double[]{1.25,15}, new int[] {4,5,60}),
			new DomainARInfo(new double[]{1.25,20}, new int[] {4,5,80}),
			new DomainARInfo(new double[]{1.25,30}, new int[] {4,5,120}),
			new DomainARInfo(new double[]{1.33333333333333,1.33333333333333}, new int[] {3,4,4}),
			new DomainARInfo(new double[]{1.33333333333333,1.66666666666666}, new int[] {3,4,5}),
			new DomainARInfo(new double[]{1.33333333333333,2}, new int[] {3,4,6}),
			new DomainARInfo(new double[]{1.33333333333333,2.5}, new int[] {6,8,15}),
			new DomainARInfo(new double[]{1.33333333333333,2.66666666666666}, new int[] {3,4,8}),
			new DomainARInfo(new double[]{1.33333333333333,3.33333333333333}, new int[] {3,4,10}),
			new DomainARInfo(new double[]{1.33333333333333,4}, new int[] {3,4,12}),
			new DomainARInfo(new double[]{1.33333333333333,5}, new int[] {3,4,15}),
			new DomainARInfo(new double[]{1.33333333333333,5.33333333333333}, new int[] {3,4,16}),
			new DomainARInfo(new double[]{1.33333333333333,6.66666666666666}, new int[] {3,4,20}),
			new DomainARInfo(new double[]{1.33333333333333,8}, new int[] {3,4,24}),
			new DomainARInfo(new double[]{1.33333333333333,10}, new int[] {3,4,30}),
			new DomainARInfo(new double[]{1.33333333333333,13.3333333333333}, new int[] {3,4,40}),
			new DomainARInfo(new double[]{1.33333333333333,16}, new int[] {3,4,48}),
			new DomainARInfo(new double[]{1.33333333333333,20}, new int[] {3,4,60}),
			new DomainARInfo(new double[]{1.33333333333333,26.6666666666666}, new int[] {3,4,80}),
			new DomainARInfo(new double[]{1.4,1.4}, new int[] {5,7,7}),
			new DomainARInfo(new double[]{1.4,1.6}, new int[] {5,7,8}),
			new DomainARInfo(new double[]{1.4,2}, new int[] {5,7,10}),
			new DomainARInfo(new double[]{1.42857142857142,1.42857142857142}, new int[] {7,10,10}),
			new DomainARInfo(new double[]{1.42857142857142,1.71428571428571}, new int[] {7,10,12}),
			new DomainARInfo(new double[]{1.42857142857142,2}, new int[] {7,10,14}),
			new DomainARInfo(new double[]{1.5,1.5}, new int[] {2,3,3}),
			new DomainARInfo(new double[]{1.5,1.75}, new int[] {4,6,7}),
			new DomainARInfo(new double[]{1.5,2}, new int[] {2,3,4}),
			new DomainARInfo(new double[]{1.5,2.5}, new int[] {2,3,5}),
			new DomainARInfo(new double[]{1.5,3}, new int[] {2,3,6}),
			new DomainARInfo(new double[]{1.5,3.75}, new int[] {4,6,15}),
			new DomainARInfo(new double[]{1.5,4}, new int[] {2,3,8}),
			new DomainARInfo(new double[]{1.5,5}, new int[] {2,3,10}),
			new DomainARInfo(new double[]{1.5,6}, new int[] {2,3,12}),
			new DomainARInfo(new double[]{1.5,7.5}, new int[] {2,3,15}),
			new DomainARInfo(new double[]{1.5,8}, new int[] {2,3,16}),
			new DomainARInfo(new double[]{1.5,10}, new int[] {2,3,20}),
			new DomainARInfo(new double[]{1.5,12}, new int[] {2,3,24}),
			new DomainARInfo(new double[]{1.5,15}, new int[] {2,3,30}),
			new DomainARInfo(new double[]{1.5,20}, new int[] {2,3,40}),
			new DomainARInfo(new double[]{1.5,24}, new int[] {2,3,48}),
			new DomainARInfo(new double[]{1.5,30}, new int[] {2,3,60}),
			new DomainARInfo(new double[]{1.6,1.6}, new int[] {5,8,8}),
			new DomainARInfo(new double[]{1.6,2}, new int[] {5,8,10}),
			new DomainARInfo(new double[]{1.6,2.4}, new int[] {5,8,12}),
			new DomainARInfo(new double[]{1.6,3}, new int[] {5,8,15}),
			new DomainARInfo(new double[]{1.6,3.2}, new int[] {5,8,16}),
			new DomainARInfo(new double[]{1.6,4}, new int[] {5,8,20}),
			new DomainARInfo(new double[]{1.6,4.8}, new int[] {5,8,24}),
			new DomainARInfo(new double[]{1.6,6}, new int[] {5,8,30}),
			new DomainARInfo(new double[]{1.6,8}, new int[] {5,8,40}),
			new DomainARInfo(new double[]{1.6,9.6}, new int[] {5,8,48}),
			new DomainARInfo(new double[]{1.6,12}, new int[] {5,8,60}),
			new DomainARInfo(new double[]{1.6,16}, new int[] {5,8,80}),
			new DomainARInfo(new double[]{1.6,24}, new int[] {5,8,120}),
			new DomainARInfo(new double[]{1.66666666666666,1.66666666666666}, new int[] {3,5,5}),
			new DomainARInfo(new double[]{1.66666666666666,2}, new int[] {3,5,6}),
			new DomainARInfo(new double[]{1.66666666666666,2.5}, new int[] {6,10,15}),
			new DomainARInfo(new double[]{1.66666666666666,2.66666666666666}, new int[] {3,5,8}),
			new DomainARInfo(new double[]{1.66666666666666,3.33333333333333}, new int[] {3,5,10}),
			new DomainARInfo(new double[]{1.66666666666666,4}, new int[] {3,5,12}),
			new DomainARInfo(new double[]{1.66666666666666,5}, new int[] {3,5,15}),
			new DomainARInfo(new double[]{1.66666666666666,5.33333333333333}, new int[] {3,5,16}),
			new DomainARInfo(new double[]{1.66666666666666,6.66666666666666}, new int[] {3,5,20}),
			new DomainARInfo(new double[]{1.66666666666666,8}, new int[] {3,5,24}),
			new DomainARInfo(new double[]{1.66666666666666,10}, new int[] {3,5,30}),
			new DomainARInfo(new double[]{1.66666666666666,13.3333333333333}, new int[] {3,5,40}),
			new DomainARInfo(new double[]{1.66666666666666,16}, new int[] {3,5,48}),
			new DomainARInfo(new double[]{1.66666666666666,20}, new int[] {3,5,60}),
			new DomainARInfo(new double[]{1.66666666666666,26.6666666666666}, new int[] {3,5,80}),
			new DomainARInfo(new double[]{1.71428571428571,1.71428571428571}, new int[] {7,12,12}),
			new DomainARInfo(new double[]{1.71428571428571,2}, new int[] {7,12,14}),
			new DomainARInfo(new double[]{1.75,1.75}, new int[] {4,7,7}),
			new DomainARInfo(new double[]{1.75,2}, new int[] {4,7,8}),
			new DomainARInfo(new double[]{2,2}, new int[] {1,2,2}),
			new DomainARInfo(new double[]{2,2.4}, new int[] {5,10,12}),
			new DomainARInfo(new double[]{2,2.5}, new int[] {2,4,5}),
			new DomainARInfo(new double[]{2,2.66666666666666}, new int[] {3,6,8}),
			new DomainARInfo(new double[]{2,3}, new int[] {1,2,3}),
			new DomainARInfo(new double[]{2,3.2}, new int[] {5,10,16}),
			new DomainARInfo(new double[]{2,3.33333333333333}, new int[] {3,6,10}),
			new DomainARInfo(new double[]{2,3.75}, new int[] {4,8,15}),
			new DomainARInfo(new double[]{2,4}, new int[] {1,2,4}),
			new DomainARInfo(new double[]{2,4.8}, new int[] {5,10,24}),
			new DomainARInfo(new double[]{2,5}, new int[] {1,2,5}),
			new DomainARInfo(new double[]{2,5.33333333333333}, new int[] {3,6,16}),
			new DomainARInfo(new double[]{2,6}, new int[] {1,2,6}),
			new DomainARInfo(new double[]{2,6.66666666666666}, new int[] {3,6,20}),
			new DomainARInfo(new double[]{2,7.5}, new int[] {2,4,15}),
			new DomainARInfo(new double[]{2,8}, new int[] {1,2,8}),
			new DomainARInfo(new double[]{2,9.6}, new int[] {5,10,48}),
			new DomainARInfo(new double[]{2,10}, new int[] {1,2,10}),
			new DomainARInfo(new double[]{2,12}, new int[] {1,2,12}),
			new DomainARInfo(new double[]{2,13.3333333333333}, new int[] {3,6,40}),
			new DomainARInfo(new double[]{2,15}, new int[] {1,2,15}),
			new DomainARInfo(new double[]{2,16}, new int[] {1,2,16}),
			new DomainARInfo(new double[]{2,20}, new int[] {1,2,20}),
			new DomainARInfo(new double[]{2,24}, new int[] {1,2,24}),
			new DomainARInfo(new double[]{2,26.6666666666666}, new int[] {3,6,80}),
			new DomainARInfo(new double[]{2,30}, new int[] {1,2,30}),
			new DomainARInfo(new double[]{2,40}, new int[] {1,2,40}),
			new DomainARInfo(new double[]{2,64}, new int[] {1,2,64}),
			new DomainARInfo(new double[]{2,80}, new int[] {1,2,80}),
			new DomainARInfo(new double[]{2,160}, new int[] {1,2,160}),
			new DomainARInfo(new double[]{2,320}, new int[] {1,2,320}),
			new DomainARInfo(new double[]{2.4,2.4}, new int[] {5,12,12}),
			new DomainARInfo(new double[]{2.4,3}, new int[] {5,12,15}),
			new DomainARInfo(new double[]{2.4,3.2}, new int[] {5,12,16}),
			new DomainARInfo(new double[]{2.4,4}, new int[] {5,12,20}),
			new DomainARInfo(new double[]{2.4,4.8}, new int[] {5,12,24}),
			new DomainARInfo(new double[]{2.4,6}, new int[] {5,12,30}),
			new DomainARInfo(new double[]{2.4,8}, new int[] {5,12,40}),
			new DomainARInfo(new double[]{2.4,9.6}, new int[] {5,12,48}),
			new DomainARInfo(new double[]{2.4,12}, new int[] {5,12,60}),
			new DomainARInfo(new double[]{2.4,16}, new int[] {5,12,80}),
			new DomainARInfo(new double[]{2.4,24}, new int[] {5,12,120}),
			new DomainARInfo(new double[]{2.5,2.5}, new int[] {2,5,5}),
			new DomainARInfo(new double[]{2.5,2.66666666666666}, new int[] {6,15,16}),
			new DomainARInfo(new double[]{2.5,3}, new int[] {2,5,6}),
			new DomainARInfo(new double[]{2.5,3.33333333333333}, new int[] {6,15,20}),
			new DomainARInfo(new double[]{2.5,3.75}, new int[] {4,10,15}),
			new DomainARInfo(new double[]{2.5,4}, new int[] {2,5,8}),
			new DomainARInfo(new double[]{2.5,5}, new int[] {2,5,10}),
			new DomainARInfo(new double[]{2.5,6}, new int[] {2,5,12}),
			new DomainARInfo(new double[]{2.5,6.66666666666666}, new int[] {6,15,40}),
			new DomainARInfo(new double[]{2.5,7.5}, new int[] {2,5,15}),
			new DomainARInfo(new double[]{2.5,8}, new int[] {2,5,16}),
			new DomainARInfo(new double[]{2.5,10}, new int[] {2,5,20}),
			new DomainARInfo(new double[]{2.5,12}, new int[] {2,5,24}),
			new DomainARInfo(new double[]{2.5,13.3333333333333}, new int[] {6,15,80}),
			new DomainARInfo(new double[]{2.5,15}, new int[] {2,5,30}),
			new DomainARInfo(new double[]{2.5,20}, new int[] {2,5,40}),
			new DomainARInfo(new double[]{2.5,24}, new int[] {2,5,48}),
			new DomainARInfo(new double[]{2.5,30}, new int[] {2,5,60}),
			new DomainARInfo(new double[]{2.5,40}, new int[] {2,5,80}),
			new DomainARInfo(new double[]{2.5,80}, new int[] {2,5,160}),
			new DomainARInfo(new double[]{2.5,160}, new int[] {2,5,320}),
			new DomainARInfo(new double[]{2.66666666666666,2.66666666666666}, new int[] {3,8,8}),
			new DomainARInfo(new double[]{2.66666666666666,3.33333333333333}, new int[] {3,8,10}),
			new DomainARInfo(new double[]{2.66666666666666,4}, new int[] {3,8,12}),
			new DomainARInfo(new double[]{2.66666666666666,5}, new int[] {3,8,15}),
			new DomainARInfo(new double[]{2.66666666666666,5.33333333333333}, new int[] {3,8,16}),
			new DomainARInfo(new double[]{2.66666666666666,6.66666666666666}, new int[] {3,8,20}),
			new DomainARInfo(new double[]{2.66666666666666,8}, new int[] {3,8,24}),
			new DomainARInfo(new double[]{2.66666666666666,10}, new int[] {3,8,30}),
			new DomainARInfo(new double[]{2.66666666666666,13.3333333333333}, new int[] {3,8,40}),
			new DomainARInfo(new double[]{2.66666666666666,16}, new int[] {3,8,48}),
			new DomainARInfo(new double[]{2.66666666666666,20}, new int[] {3,8,60}),
			new DomainARInfo(new double[]{2.66666666666666,26.6666666666666}, new int[] {3,8,80}),
			new DomainARInfo(new double[]{3,3}, new int[] {1,3,3}),
			new DomainARInfo(new double[]{3,3.2}, new int[] {5,15,16}),
			new DomainARInfo(new double[]{3,3.75}, new int[] {4,12,15}),
			new DomainARInfo(new double[]{3,4}, new int[] {1,3,4}),
			new DomainARInfo(new double[]{3,4.8}, new int[] {5,15,24}),
			new DomainARInfo(new double[]{3,5}, new int[] {1,3,5}),
			new DomainARInfo(new double[]{3,6}, new int[] {1,3,6}),
			new DomainARInfo(new double[]{3,7.5}, new int[] {2,6,15}),
			new DomainARInfo(new double[]{3,8}, new int[] {1,3,8}),
			new DomainARInfo(new double[]{3,9.6}, new int[] {5,15,48}),
			new DomainARInfo(new double[]{3,10}, new int[] {1,3,10}),
			new DomainARInfo(new double[]{3,12}, new int[] {1,3,12}),
			new DomainARInfo(new double[]{3,15}, new int[] {1,3,15}),
			new DomainARInfo(new double[]{3,16}, new int[] {1,3,16}),
			new DomainARInfo(new double[]{3,20}, new int[] {1,3,20}),
			new DomainARInfo(new double[]{3,24}, new int[] {1,3,24}),
			new DomainARInfo(new double[]{3,30}, new int[] {1,3,30}),
			new DomainARInfo(new double[]{3.2,3.2}, new int[] {5,16,16}),
			new DomainARInfo(new double[]{3.2,4}, new int[] {5,16,20}),
			new DomainARInfo(new double[]{3.2,4.8}, new int[] {5,16,24}),
			new DomainARInfo(new double[]{3.2,6}, new int[] {5,16,30}),
			new DomainARInfo(new double[]{3.2,8}, new int[] {5,16,40}),
			new DomainARInfo(new double[]{3.2,9.6}, new int[] {5,16,48}),
			new DomainARInfo(new double[]{3.2,12}, new int[] {5,16,60}),
			new DomainARInfo(new double[]{3.2,16}, new int[] {5,16,80}),
			new DomainARInfo(new double[]{3.2,24}, new int[] {5,16,120}),
			new DomainARInfo(new double[]{3.33333333333333,3.33333333333333}, new int[] {3,10,10}),
			new DomainARInfo(new double[]{3.33333333333333,4}, new int[] {3,10,12}),
			new DomainARInfo(new double[]{3.33333333333333,5}, new int[] {3,10,15}),
			new DomainARInfo(new double[]{3.33333333333333,5.33333333333333}, new int[] {3,10,16}),
			new DomainARInfo(new double[]{3.33333333333333,6.66666666666666}, new int[] {3,10,20}),
			new DomainARInfo(new double[]{3.33333333333333,8}, new int[] {3,10,24}),
			new DomainARInfo(new double[]{3.33333333333333,10}, new int[] {3,10,30}),
			new DomainARInfo(new double[]{3.33333333333333,13.3333333333333}, new int[] {3,10,40}),
			new DomainARInfo(new double[]{3.33333333333333,16}, new int[] {3,10,48}),
			new DomainARInfo(new double[]{3.33333333333333,20}, new int[] {3,10,60}),
			new DomainARInfo(new double[]{3.33333333333333,26.6666666666666}, new int[] {3,10,80}),
			new DomainARInfo(new double[]{3.75,3.75}, new int[] {4,15,15}),
			new DomainARInfo(new double[]{3.75,4}, new int[] {4,15,16}),
			new DomainARInfo(new double[]{3.75,5}, new int[] {4,15,20}),
			new DomainARInfo(new double[]{3.75,6}, new int[] {4,15,24}),
			new DomainARInfo(new double[]{3.75,7.5}, new int[] {4,15,30}),
			new DomainARInfo(new double[]{3.75,10}, new int[] {4,15,40}),
			new DomainARInfo(new double[]{3.75,12}, new int[] {4,15,48}),
			new DomainARInfo(new double[]{3.75,15}, new int[] {4,15,60}),
			new DomainARInfo(new double[]{3.75,20}, new int[] {4,15,80}),
			new DomainARInfo(new double[]{3.75,30}, new int[] {4,15,120}),
			new DomainARInfo(new double[]{4,4}, new int[] {1,4,4}),
			new DomainARInfo(new double[]{4,4.8}, new int[] {5,20,24}),
			new DomainARInfo(new double[]{4,5}, new int[] {1,4,5}),
			new DomainARInfo(new double[]{4,5.33333333333333}, new int[] {3,12,16}),
			new DomainARInfo(new double[]{4,6}, new int[] {1,4,6}),
			new DomainARInfo(new double[]{4,6.66666666666666}, new int[] {3,12,20}),
			new DomainARInfo(new double[]{4,7.5}, new int[] {2,8,15}),
			new DomainARInfo(new double[]{4,8}, new int[] {1,4,8}),
			new DomainARInfo(new double[]{4,9.6}, new int[] {5,20,48}),
			new DomainARInfo(new double[]{4,10}, new int[] {1,4,10}),
			new DomainARInfo(new double[]{4,12}, new int[] {1,4,12}),
			new DomainARInfo(new double[]{4,13.3333333333333}, new int[] {3,12,40}),
			new DomainARInfo(new double[]{4,15}, new int[] {1,4,15}),
			new DomainARInfo(new double[]{4,16}, new int[] {1,4,16}),
			new DomainARInfo(new double[]{4,20}, new int[] {1,4,20}),
			new DomainARInfo(new double[]{4,24}, new int[] {1,4,24}),
			new DomainARInfo(new double[]{4,26.6666666666666}, new int[] {3,12,80}),
			new DomainARInfo(new double[]{4,30}, new int[] {1,4,30}),
			new DomainARInfo(new double[]{4,40}, new int[] {1,4,40}),
			new DomainARInfo(new double[]{4,64}, new int[] {1,4,64}),
			new DomainARInfo(new double[]{4,80}, new int[] {1,4,80}),
			new DomainARInfo(new double[]{4,160}, new int[] {1,4,160}),
			new DomainARInfo(new double[]{4,320}, new int[] {1,4,320}),
			new DomainARInfo(new double[]{4.8,4.8}, new int[] {5,24,24}),
			new DomainARInfo(new double[]{4.8,6}, new int[] {5,24,30}),
			new DomainARInfo(new double[]{4.8,8}, new int[] {5,24,40}),
			new DomainARInfo(new double[]{4.8,9.6}, new int[] {5,24,48}),
			new DomainARInfo(new double[]{4.8,12}, new int[] {5,24,60}),
			new DomainARInfo(new double[]{4.8,16}, new int[] {5,24,80}),
			new DomainARInfo(new double[]{4.8,24}, new int[] {5,24,120}),
			new DomainARInfo(new double[]{5,5}, new int[] {1,5,5}),
			new DomainARInfo(new double[]{5,5.33333333333333}, new int[] {3,15,16}),
			new DomainARInfo(new double[]{5,6}, new int[] {1,5,6}),
			new DomainARInfo(new double[]{5,6.66666666666666}, new int[] {3,15,20}),
			new DomainARInfo(new double[]{5,7.5}, new int[] {2,10,15}),
			new DomainARInfo(new double[]{5,8}, new int[] {1,5,8}),
			new DomainARInfo(new double[]{5,10}, new int[] {1,5,10}),
			new DomainARInfo(new double[]{5,12}, new int[] {1,5,12}),
			new DomainARInfo(new double[]{5,13.3333333333333}, new int[] {3,15,40}),
			new DomainARInfo(new double[]{5,15}, new int[] {1,5,15}),
			new DomainARInfo(new double[]{5,16}, new int[] {1,5,16}),
			new DomainARInfo(new double[]{5,20}, new int[] {1,5,20}),
			new DomainARInfo(new double[]{5,24}, new int[] {1,5,24}),
			new DomainARInfo(new double[]{5,26.6666666666666}, new int[] {3,15,80}),
			new DomainARInfo(new double[]{5,30}, new int[] {1,5,30}),
			new DomainARInfo(new double[]{5,40}, new int[] {1,5,40}),
			new DomainARInfo(new double[]{5,64}, new int[] {1,5,64}),
			new DomainARInfo(new double[]{5,80}, new int[] {1,5,80}),
			new DomainARInfo(new double[]{5,160}, new int[] {1,5,160}),
			new DomainARInfo(new double[]{5,320}, new int[] {1,5,320}),
			new DomainARInfo(new double[]{5.33333333333333,5.33333333333333}, new int[] {3,16,16}),
			new DomainARInfo(new double[]{5.33333333333333,6.66666666666666}, new int[] {3,16,20}),
			new DomainARInfo(new double[]{5.33333333333333,8}, new int[] {3,16,24}),
			new DomainARInfo(new double[]{5.33333333333333,10}, new int[] {3,16,30}),
			new DomainARInfo(new double[]{5.33333333333333,13.3333333333333}, new int[] {3,16,40}),
			new DomainARInfo(new double[]{5.33333333333333,16}, new int[] {3,16,48}),
			new DomainARInfo(new double[]{5.33333333333333,20}, new int[] {3,16,60}),
			new DomainARInfo(new double[]{5.33333333333333,26.6666666666666}, new int[] {3,16,80}),
			new DomainARInfo(new double[]{6,6}, new int[] {1,6,6}),
			new DomainARInfo(new double[]{6,7.5}, new int[] {2,12,15}),
			new DomainARInfo(new double[]{6,8}, new int[] {1,6,8}),
			new DomainARInfo(new double[]{6,9.6}, new int[] {5,30,48}),
			new DomainARInfo(new double[]{6,10}, new int[] {1,6,10}),
			new DomainARInfo(new double[]{6,12}, new int[] {1,6,12}),
			new DomainARInfo(new double[]{6,15}, new int[] {1,6,15}),
			new DomainARInfo(new double[]{6,16}, new int[] {1,6,16}),
			new DomainARInfo(new double[]{6,20}, new int[] {1,6,20}),
			new DomainARInfo(new double[]{6,24}, new int[] {1,6,24}),
			new DomainARInfo(new double[]{6,30}, new int[] {1,6,30}),
			new DomainARInfo(new double[]{6.66666666666666,6.66666666666666}, new int[] {3,20,20}),
			new DomainARInfo(new double[]{6.66666666666666,8}, new int[] {3,20,24}),
			new DomainARInfo(new double[]{6.66666666666666,10}, new int[] {3,20,30}),
			new DomainARInfo(new double[]{6.66666666666666,13.3333333333333}, new int[] {3,20,40}),
			new DomainARInfo(new double[]{6.66666666666666,16}, new int[] {3,20,48}),
			new DomainARInfo(new double[]{6.66666666666666,20}, new int[] {3,20,60}),
			new DomainARInfo(new double[]{6.66666666666666,26.6666666666666}, new int[] {3,20,80}),
			new DomainARInfo(new double[]{7.5,7.5}, new int[] {2,15,15}),
			new DomainARInfo(new double[]{7.5,8}, new int[] {2,15,16}),
			new DomainARInfo(new double[]{7.5,10}, new int[] {2,15,20}),
			new DomainARInfo(new double[]{7.5,12}, new int[] {2,15,24}),
			new DomainARInfo(new double[]{7.5,15}, new int[] {2,15,30}),
			new DomainARInfo(new double[]{7.5,20}, new int[] {2,15,40}),
			new DomainARInfo(new double[]{7.5,24}, new int[] {2,15,48}),
			new DomainARInfo(new double[]{7.5,30}, new int[] {2,15,60}),
			new DomainARInfo(new double[]{8,8}, new int[] {1,8,8}),
			new DomainARInfo(new double[]{8,9.6}, new int[] {5,40,48}),
			new DomainARInfo(new double[]{8,10}, new int[] {1,8,10}),
			new DomainARInfo(new double[]{8,12}, new int[] {1,8,12}),
			new DomainARInfo(new double[]{8,13.3333333333333}, new int[] {3,24,40}),
			new DomainARInfo(new double[]{8,15}, new int[] {1,8,15}),
			new DomainARInfo(new double[]{8,16}, new int[] {1,8,16}),
			new DomainARInfo(new double[]{8,20}, new int[] {1,8,20}),
			new DomainARInfo(new double[]{8,24}, new int[] {1,8,24}),
			new DomainARInfo(new double[]{8,26.6666666666666}, new int[] {3,24,80}),
			new DomainARInfo(new double[]{8,30}, new int[] {1,8,30}),
			new DomainARInfo(new double[]{8,40}, new int[] {1,8,40}),
			new DomainARInfo(new double[]{8,64}, new int[] {1,8,64}),
			new DomainARInfo(new double[]{8,80}, new int[] {1,8,80}),
			new DomainARInfo(new double[]{8,160}, new int[] {1,8,160}),
			new DomainARInfo(new double[]{8,320}, new int[] {1,8,320}),
			new DomainARInfo(new double[]{9.6,9.6}, new int[] {5,48,48}),
			new DomainARInfo(new double[]{9.6,12}, new int[] {5,48,60}),
			new DomainARInfo(new double[]{9.6,16}, new int[] {5,48,80}),
			new DomainARInfo(new double[]{9.6,24}, new int[] {5,48,120}),
			new DomainARInfo(new double[]{10,10}, new int[] {1,10,10}),
			new DomainARInfo(new double[]{10,12}, new int[] {1,10,12}),
			new DomainARInfo(new double[]{10,13.3333333333333}, new int[] {3,30,40}),
			new DomainARInfo(new double[]{10,15}, new int[] {1,10,15}),
			new DomainARInfo(new double[]{10,16}, new int[] {1,10,16}),
			new DomainARInfo(new double[]{10,20}, new int[] {1,10,20}),
			new DomainARInfo(new double[]{10,24}, new int[] {1,10,24}),
			new DomainARInfo(new double[]{10,26.6666666666666}, new int[] {3,30,80}),
			new DomainARInfo(new double[]{10,30}, new int[] {1,10,30}),
			new DomainARInfo(new double[]{10,40}, new int[] {1,10,40}),
			new DomainARInfo(new double[]{10,64}, new int[] {1,10,64}),
			new DomainARInfo(new double[]{10,80}, new int[] {1,10,80}),
			new DomainARInfo(new double[]{10,160}, new int[] {1,10,160}),
			new DomainARInfo(new double[]{10,320}, new int[] {1,10,320}),
			new DomainARInfo(new double[]{12,12}, new int[] {1,12,12}),
			new DomainARInfo(new double[]{12,15}, new int[] {1,12,15}),
			new DomainARInfo(new double[]{12,16}, new int[] {1,12,16}),
			new DomainARInfo(new double[]{12,20}, new int[] {1,12,20}),
			new DomainARInfo(new double[]{12,24}, new int[] {1,12,24}),
			new DomainARInfo(new double[]{12,30}, new int[] {1,12,30}),
			new DomainARInfo(new double[]{13.3333333333333,13.3333333333333}, new int[] {3,40,40}),
			new DomainARInfo(new double[]{13.3333333333333,16}, new int[] {3,40,48}),
			new DomainARInfo(new double[]{13.3333333333333,20}, new int[] {3,40,60}),
			new DomainARInfo(new double[]{13.3333333333333,26.6666666666666}, new int[] {3,40,80}),
			new DomainARInfo(new double[]{15,15}, new int[] {1,15,15}),
			new DomainARInfo(new double[]{15,16}, new int[] {1,15,16}),
			new DomainARInfo(new double[]{15,20}, new int[] {1,15,20}),
			new DomainARInfo(new double[]{15,24}, new int[] {1,15,24}),
			new DomainARInfo(new double[]{15,30}, new int[] {1,15,30}),
			new DomainARInfo(new double[]{16,16}, new int[] {1,16,16}),
			new DomainARInfo(new double[]{16,20}, new int[] {1,16,20}),
			new DomainARInfo(new double[]{16,24}, new int[] {1,16,24}),
			new DomainARInfo(new double[]{16,26.6666666666666}, new int[] {3,48,80}),
			new DomainARInfo(new double[]{16,30}, new int[] {1,16,30}),
			new DomainARInfo(new double[]{16,40}, new int[] {1,16,40}),
			new DomainARInfo(new double[]{16,64}, new int[] {1,16,64}),
			new DomainARInfo(new double[]{16,80}, new int[] {1,16,80}),
			new DomainARInfo(new double[]{16,160}, new int[] {1,16,160}),
			new DomainARInfo(new double[]{16,320}, new int[] {1,16,320}),
			new DomainARInfo(new double[]{20,20}, new int[] {1,20,20}),
			new DomainARInfo(new double[]{20,24}, new int[] {1,20,24}),
			new DomainARInfo(new double[]{20,26.6666666666666}, new int[] {3,60,80}),
			new DomainARInfo(new double[]{20,30}, new int[] {1,20,30}),
			new DomainARInfo(new double[]{20,40}, new int[] {1,20,40}),
			new DomainARInfo(new double[]{20,64}, new int[] {1,20,64}),
			new DomainARInfo(new double[]{20,80}, new int[] {1,20,80}),
			new DomainARInfo(new double[]{20,160}, new int[] {1,20,160}),
			new DomainARInfo(new double[]{20,320}, new int[] {1,20,320}),
			new DomainARInfo(new double[]{24,24}, new int[] {1,24,24}),
			new DomainARInfo(new double[]{24,30}, new int[] {1,24,30}),
			new DomainARInfo(new double[]{26.6666666666666,26.6666666666666}, new int[] {3,80,80}),
			new DomainARInfo(new double[]{30,30}, new int[] {1,30,30}),
			new DomainARInfo(new double[]{32,40}, new int[] {1,32,40}),
			new DomainARInfo(new double[]{32,64}, new int[] {1,32,64}),
			new DomainARInfo(new double[]{32,80}, new int[] {1,32,80}),
			new DomainARInfo(new double[]{32,160}, new int[] {1,32,160}),
			new DomainARInfo(new double[]{32,320}, new int[] {1,32,320}),
			new DomainARInfo(new double[]{40,40}, new int[] {1,40,40}),
			new DomainARInfo(new double[]{40,64}, new int[] {1,40,64}),
			new DomainARInfo(new double[]{40,80}, new int[] {1,40,80}),
			new DomainARInfo(new double[]{40,160}, new int[] {1,40,160}),
			new DomainARInfo(new double[]{40,320}, new int[] {1,40,320}),
			new DomainARInfo(new double[]{64,64}, new int[] {1,64,64}),
			new DomainARInfo(new double[]{64,80}, new int[] {1,64,80}),
			new DomainARInfo(new double[]{64,160}, new int[] {1,64,160}),
			new DomainARInfo(new double[]{64,320}, new int[] {1,64,320}),
			new DomainARInfo(new double[]{80,80}, new int[] {1,80,80}),
			new DomainARInfo(new double[]{80,160}, new int[] {1,80,160}),
			new DomainARInfo(new double[]{80,320}, new int[] {1,80,320}),
			new DomainARInfo(new double[]{160,160}, new int[] {1,160,160}),
			new DomainARInfo(new double[]{160,320}, new int[] {1,160,320}),
			new DomainARInfo(new double[]{320,320}, new int[] {1,320,320}),
	};

	public static class ChomboMeshRecommendation {
		private int dim;
		public List<ChomboMeshSpec> validMeshSpecList;
		public List<int[]> recommendedNxList;
		public double[] currentAR;
		public int[] bestRecommendedNx;
		private String[] dialogOptions;
		private String errorMessage;
		public static final String optionClose = "Close";
		public static final String optionSuggestions = "Domain Aspect Ratio Suggestions";
		
		private ChomboMeshRecommendation(int dim)
		{
			this.dim = dim;
		}
		
		public String getMeshSuggestions()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Domain sizes that are proportional to the following values are compatible with "
					+ SolverDescription.Chombo.getShortDisplayLabel() + " solver:\n");
			for (int[] nx : recommendedNxList)
			{
				sb.append(nx[0]);
				if (dim > 1)
				{
					sb.append(" : ").append(nx[1]);
					if (dim > 2)
					{
						sb.append(" : ").append(nx[2]);
					}
				}
				sb.append("\n");
			}
			return sb.toString();
		}
		
		public boolean validate()
		{
			boolean bGood = true;
			if (validMeshSpecList == null || validMeshSpecList.size() == 0) {
				bGood = false;
				dialogOptions = new String[]{optionClose};
				errorMessage = SolverDescription.Chombo.getShortDisplayLabel()
						+ " solver does not work with arbitrary geometry domain size. "
						+ "This domain's sizes are proportional to the following aspect ratios: "
						+ currentAR[0] + (dim > 1 ? " : " + currentAR[1] : "")
						+ (dim > 2 ? " : " + currentAR[2] : "")
						+ " which is incompatible with this solver. Try creating a new geometry (in a new application/model), "
						+ "with domain sizes proportional to an aspect ratio that is compatible with the solver, for example, "
						+ bestRecommendedNx[0] + (dim > 1 ? " : " + bestRecommendedNx[1] : "")
						+ (dim > 2 ? " : " + bestRecommendedNx[2] : "")
						+ ".";
				if (recommendedNxList != null && recommendedNxList.size() > 0) {
					errorMessage += " For mesh size suggestions, click \"" + optionSuggestions + "\" below.";
					dialogOptions = new String[] {optionClose, optionSuggestions};
				}
			}
			return bGood;
		}
		
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
		public String[] getDialogOptions() {
			return dialogOptions;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
	}

	public static class ChomboMeshSpec {
		public int[] Nx;
		public double H;

		public ChomboMeshSpec(int[] n, double h) {
			super();
			Nx = n;
			H = h;
		}

		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}

		public String getFormattedH() {
			return String.format("%20.5e", H);
		}
	}

	private static final int[] NxFactors = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, 25, 27, 28,
			30, 32, 35, 36, 40, 42, 45, 48, 50, 54, 56, 60, 63, 64, 70, 72, 75, 80, 81, 84, 90, 96, 100, 105, 108, 112, 120,
			125, 126, 128, 135, 140, 144, 150, 160, 162, 168, 175, 180, 189, 192, 200, 210, 216, 224, 225, 240, 243, 250, 252,
			256, 270, 280, 288, 300, 315, 320, 324, 336, 350, 360, 375, 378, 384, 400, 405, 420, 432, 448, 450, 480, 486,
			500 };

	public ChomboMeshValidator(Geometry geometry) {
		this.dim = geometry.getDimension();
		this.extent = geometry.getExtent();
		this.blockFactor = ChomboSolverSpec.BLOCK_FACTOR;
	}

	public ChomboMeshValidator(int dim, Extent extent, int blockFactor) {
		this.dim = dim;
		this.extent = extent;
		this.blockFactor = blockFactor;
	}

	/**
	 * sort x, y, z as indexes based on extent
	 * 
	 * @param extentValues
	 * @return
	 */
	private int[] sortDir(double[] extentValues) {
		int[] orderIndexes = new int[] { 0, 1, 2 };

		if (extentValues[0] > extentValues[1]) {
			orderIndexes[0] = 1;
			orderIndexes[1] = 0;
		}
		if (dim == 3) {
			if (extentValues[2] < extentValues[orderIndexes[0]]) {
				orderIndexes[2] = orderIndexes[1];
				orderIndexes[1] = orderIndexes[0];
				orderIndexes[0] = 2;
			} else if (extentValues[2] < extentValues[orderIndexes[1]]) {
				orderIndexes[2] = orderIndexes[1];
				orderIndexes[1] = 2;
			}
		}
		return orderIndexes;
	}

	public ChomboMeshRecommendation computeMeshSpecs() {
		final double tol = 1e-5;

		ChomboMeshRecommendation chomboMeshRecommendation = new ChomboMeshRecommendation(dim);

		double[] extentValues = new double[] { extent.getX(), extent.getY(), extent.getZ() };
		int[] orderedIndexes = sortDir(extentValues);
		double minExtent = extentValues[orderedIndexes[0]];

		double[] AR = new double[dim];
		for (int d = 0; d < dim; ++d) {
			AR[d] = extentValues[orderedIndexes[d]] / minExtent;
		}
		int arIndex = -1;
		DomainARInfo[] listDomainARInfo = dim == 2 ? listARDomainInfo2D : listARDomainInfo3D;
		for (int i = 0; i < listDomainARInfo.length; ++i) {
			boolean bMatch = true;
			for (int d = 0; d < dim - 1; ++d) {
				bMatch &= Math.abs(AR[d + 1] - listDomainARInfo[i].ar[d]) < tol;
			}
			if (bMatch) {
				arIndex = i;
				break;
			}
		}
		if (arIndex == -1) {
			chomboMeshRecommendation.recommendedNxList = new ArrayList<>();
			chomboMeshRecommendation.currentAR = new double[dim];
			chomboMeshRecommendation.bestRecommendedNx = new int[dim];

			double tolAR_dist = 0.5;
			int i_match = -1;
			double AR_MinDistance = Double.MAX_VALUE; // just give it a large value
			for (int i = 0; i < listDomainARInfo.length; ++i) {
				double ar_dist = 0;
				for (int d = 0; d < dim - 1; ++d) {
					double thisDist = Math.abs(AR[d + 1] - listDomainARInfo[i].ar[d])/ AR[d + 1];
					ar_dist = Math.max(ar_dist, thisDist);
				}
				if (ar_dist < AR_MinDistance) {
					AR_MinDistance = ar_dist;
					i_match = i;
					if (ar_dist <= tolAR_dist) {
						int[] recommendedNx = new int[dim];
						for (int d = 0; d < dim; ++d) {
							recommendedNx[orderedIndexes[d]] = listDomainARInfo[i].minNx[d];
						}

						chomboMeshRecommendation.recommendedNxList.add(recommendedNx);
					}
				}
			}
			Collections.sort(chomboMeshRecommendation.recommendedNxList, new Comparator<int[]>() {

				@Override
				public int compare(int[] o1, int[] o2) {
					if (o1[0] < o2[0]) {
						return -1;
					}
					if (o1[0] > o2[0]) {
						return 1;
					}

					if (dim > 1) {
						if (o1[1] < o2[1]) {
							return -1;
						}
						if (o1[1] > o2[1]) {
							return 1;
						}

						if (dim > 2) {
							if (o1[2] < o2[2]) {
								return -1;
							}
							if (o1[2] > o2[2]) {
								return 1;
							}
						}
					}
					return 0;
				}
			});
			for (int d = 0; d < dim; ++d) {
				chomboMeshRecommendation.bestRecommendedNx[orderedIndexes[d]] = listDomainARInfo[i_match].minNx[d];
			}
			// compute AR again in original order
			chomboMeshRecommendation.currentAR[0] = extent.getX() / minExtent;
			if (dim > 1) {
				chomboMeshRecommendation.currentAR[1] = extent.getY() / minExtent;
				if (dim > 2) {
					chomboMeshRecommendation.currentAR[2] = extent.getZ() / minExtent;
				}
			}
		} else {
			chomboMeshRecommendation.validMeshSpecList = new ArrayList<>();

			int[] coarseNx = new int[dim];
			for (int d = 0; d < dim; ++d) {
				coarseNx[d] = blockFactor * listDomainARInfo[arIndex].minNx[d];
			}

			long MaxNAllowed= (long)5.0E+7; // limit max number of mesh points
			for (int nextNx : NxFactors) {
				int[] Nx = new int[dim];
				int totalN = 1; // compute total number of points
				for (int d = 0; d < dim; ++d) {
					int dir = orderedIndexes[d];
					Nx[dir] = nextNx * coarseNx[d];
					totalN = totalN*Nx[dir];
				}
				if (totalN < MaxNAllowed) {
					double H = extent.getX() / Nx[0];
					chomboMeshRecommendation.validMeshSpecList.add(new ChomboMeshSpec(Nx, H));
				}
			}
		}
		return chomboMeshRecommendation;
	}

	public static void main(String[] args) {
		int blockFactor = 4;
//		Extent[] extents2D = new Extent[] { new Extent(1, 1, 1), new Extent(2, 3, 1), new Extent(5.00001, 2.5, 1) };
//		for (Extent extent : extents2D) {
//			ChomboMeshValidator validator = new ChomboMeshValidator(2, extent, blockFactor);
//			ChomboMeshRecommendation chomboMeshRecommendation = validator.computeMeshSpecs();
//			System.out.println("==Test Case 2D==" + validator);
//			for (ChomboMeshSpec ms : chomboMeshRecommendation.validMeshSpecList) {
//				System.out.println(ms);
//			}
//		}
		Extent[] extents3D = new Extent[] { new Extent(158, 34, 4) };
		for (Extent extent : extents3D) {
			ChomboMeshValidator validator = new ChomboMeshValidator(3, extent, blockFactor);
			ChomboMeshRecommendation chomboMeshRecommendation = validator.computeMeshSpecs();
			System.out.println("==Test Case 3D==" + validator);
			System.out.println(chomboMeshRecommendation);
		}
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
