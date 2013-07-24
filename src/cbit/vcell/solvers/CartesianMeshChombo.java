package cbit.vcell.solvers;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.vcell.geometry.SampledCurve;

@SuppressWarnings("serial")
public class CartesianMeshChombo extends CartesianMesh {

	public static class SurfaceTriangleEntry3D implements Serializable {
		public int memIndex;
		public int faceIndex = 0;
		public Coordinate p0;
		public Coordinate p1;
		public Coordinate p2;
	}
	
	public static class Segment2D implements Serializable {
		public int memIndex;
		public int prevVertex;
		public int nextVertex;
		public int prevNeigbhor;
		public int nextNeigbhor;
		
		public Segment2D(int memIndex, int prevVertex, int nextVertex,
				int prevNeigbhor, int nextNeigbhor) {
			super();
			this.memIndex = memIndex;
			this.prevVertex = prevVertex;
			this.nextVertex = nextVertex;
			this.prevNeigbhor = prevNeigbhor;
			this.nextNeigbhor = nextNeigbhor;
		}
		
	}
	
	public static class SliceViewEntry implements Serializable {
		public int memIndex;
		public double[] crossPoints;
	}
	
//	public static class MeshMetricsEntry implements Serializable
//	{
//		public int index,i,j,k;
//		//public double x,y,z;
//		//public double normalx, normaly, normalz;
//		//double volFrac;
//		//double areaFrac;
//		public MeshMetricsEntry(int index, int i, int j, int k
////				, double x, double y, double z, double normalx, double normaly,
////				double normalz, double volFrac, double areaFrac
//				) 
//		{
//			super();
//			this.index = index;
//			this.i = i;
//			this.j = j;
//			this.k = k;
////			this.x = x;
////			this.y = y;
////			this.z = z;
////			this.normalx = normalx;
////			this.normaly = normaly;
////			this.normalz = normalz;
////			this.volFrac = volFrac;
////			this.areaFrac = areaFrac;
//		}		
//	}
		
	private int dimension = 0;

	private transient double[] dx;
//	private transient List<ChomboBox> boxList = new ArrayList<ChomboBox>();
	
	// 3D
	private transient Map<Integer, List<SurfaceTriangleEntry3D>> surfaceTriangleMap = new HashMap<Integer, List<SurfaceTriangleEntry3D>>();
//	private transient List<MeshMetricsEntry> metrics = new ArrayList<MeshMetricsEntry>();
	private transient List<SliceViewEntry> sliceViewList = new ArrayList<SliceViewEntry>();
	
	// 2D
	private transient Coordinate[] vertices = null;
	private transient Segment2D[] segments = null;
	
//	private static final String ROOT_GROUP = "/";
//	private static final String MESH_GROUP = "/mesh";
	private static final String BOXES_DATASET = "boxes";
	private static final String METRICS_DATASET = "metrics";
	private static final String VERTICES_DATASET = "vertices";
	private static final String SEGMENTS_DATASET = "segments";
	private static final String SURFACE_DATASET = "surface triangles";
	private static final String SLICE_VIEW_DATASET = "slice view";
	private static final String MESH_ATTR_DIMENSION = "dimension";
	private static final String MESH_ATTR_ORIGIN = "origin";
	private static final String MESH_ATTR_EXTENT = "extent";
	private static final String MESH_ATTR_NX = "Nx";
	private static final String MESH_ATTR_DX = "Dx";
	
	public static CartesianMeshChombo readMeshFile(File chomboMeshFile) throws Exception {
		CartesianMeshChombo chomboMesh = new CartesianMeshChombo();
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshFile = fileFormat.createInstance(chomboMeshFile.getAbsolutePath(), FileFormat.READ);
		meshFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		Group meshGroup = (Group)rootGroup.getMemberList().get(0);
		List<Attribute> meshAttrList = meshGroup.getMetadata();
		
		int[] nx = new int[3];
		Arrays.fill(nx, 1);
		chomboMesh.dx = new double[3];
		double[] extent = new double[3];
		Arrays.fill(extent, 1);
		double[] origin = new double[3];
		
		for (Attribute attr : meshAttrList)
		{
			String attrName = attr.getName();
			Object value = attr.getValue();
			if (attrName.equals(MESH_ATTR_DIMENSION))
			{
				chomboMesh.dimension = ((int[])value)[0];
			}
			else
			{
				String[] valueStrArray = (String[])value;
				String value0 = valueStrArray[0];
				StringTokenizer st = new StringTokenizer(value0, "{,} ");
				List<Double> valueList = new ArrayList<Double>();
				while (st.hasMoreTokens())
				{
					String token = st.nextToken();
					valueList.add(Double.parseDouble(token));
				}
				if (attrName.equals(MESH_ATTR_DX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.dx[i] = valueList.get(i);
					}
				}
				else if (attrName.equals(MESH_ATTR_EXTENT))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						extent[i] = valueList.get(i);
					}
					chomboMesh.extent = new Extent(extent[0], extent[1], extent[2]);
				}
				else if (attrName.equals(MESH_ATTR_NX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						nx[i] = valueList.get(i).intValue();
					}
					chomboMesh.size = new ISize(nx[0], nx[1], nx[2]);
				}
				else if (attrName.equals(MESH_ATTR_ORIGIN))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						origin[i] = valueList.get(i);
					}
					chomboMesh.origin = new Origin(origin[0], origin[1], origin[2]);
				}
			}
		}
		
		List<HObject> memberList = meshGroup.getMemberList();
		for (HObject member : memberList)
		{
			if (member instanceof Dataset)
			{
				Dataset dataset = (Dataset)member;
				Vector vectValues = (Vector)dataset.read();
				String name = dataset.getName();
				if (name.equals(BOXES_DATASET))
				{
					// not needed right now
				}
				else if (name.equals(VERTICES_DATASET))
				{
					int c = -1;
					double[] x = (double[]) vectValues.get(++ c);
					double[] y = (double[]) vectValues.get(++ c);
					double[] z = null;
					if (chomboMesh.dimension == 3)
					{
						z = (double[]) vectValues.get(++ c);
					}
					chomboMesh.vertices = new Coordinate[x.length];
					for (int i = 0; i < x.length; ++ i)
					{
						chomboMesh.vertices[i] = new Coordinate(x[i], y[i], z == null ? 0 : z[i]);
					}
				}
				else if (name.equals(SEGMENTS_DATASET))
				{
					int c = -1;
					int[] index = (int[]) vectValues.get(++ c);
					int[] prevVertex = (int[]) vectValues.get(++ c);
					int[] nextVertex = (int[]) vectValues.get(++ c);
					int[] prevNeighbor = (int[]) vectValues.get(++ c);
					int[] nextNeighbor = (int[]) vectValues.get(++ c);
					double[] z = null;
					if (chomboMesh.dimension == 3)
					{
						z = (double[]) vectValues.get(++ c);
					}
					chomboMesh.segments = new Segment2D[index.length];
					for (int i = 0; i < index.length; ++ i)
					{
						chomboMesh.segments[i] = new Segment2D(index[i], prevVertex[i], nextVertex[i], prevNeighbor[i], nextNeighbor[i]);
					}
				}
				else if (name.equals(METRICS_DATASET))
				{
					int c = -1;
					int[] index = (int[]) vectValues.get(++ c);
					int[] i = (int[]) vectValues.get(++ c);
					int[] j = (int[]) vectValues.get(++ c);
					int[] k = null;
					if (chomboMesh.dimension == 3)
					{
						k = (int[]) vectValues.get(++ c);
					}
					double[] x = (double[])vectValues.get(++ c);
					double[] y = (double[])vectValues.get(++ c);
					double[] z = null;
					if (chomboMesh.dimension == 3)
					{
						z = (double[])vectValues.get(++ c);
					}
					double[] normalx = (double[])vectValues.get(++ c);
					double[] normaly = (double[])vectValues.get(++ c);
					double[] normalz = null;
					if (chomboMesh.dimension == 3)
					{
						normalz = (double[])vectValues.get(++ c);
					}
					double[] volFrac = (double[])vectValues.get(++ c);
					double[] areaFrac = (double[])vectValues.get(++ c);
					double unitVol = chomboMesh.dx[0] * chomboMesh.dx[1] * chomboMesh.dx[2];
					chomboMesh.membraneElements = new MembraneElement[index.length];
					for (int n = 0; n < index.length; ++ n)
					{
						int insideIndex = (k == null ? 0 : k[n] * chomboMesh.size.getY() * chomboMesh.size.getX())
								+ j[n] * chomboMesh.size.getX() + i[n];
						int outsideIndex = insideIndex;
						double area = areaFrac[n] * unitVol;
						chomboMesh.membraneElements[n] = new MembraneElement(index[n], insideIndex, outsideIndex,
								-1, -1, -1, -1,
								(float)area,
								(float)normalx[n],
								(float)normaly[n],
								(float)(normalz == null ? 0 : normalz[n]),
								(float)x[n],
								(float)y[n],
								(float)(z == null ? 0 : z[n]));
					}
				}
				else if (name.equals(SURFACE_DATASET))
				{
					int c = 0;
					int[] index = (int[]) vectValues.get(c);
					int[] faceIndex = (int[]) vectValues.get(++ c);
					int[] neighbor = (int[])vectValues.get(++ c);
					double[] x0 = (double[])vectValues.get(++ c);
					double[] y0 = (double[])vectValues.get(++ c);
					double[] z0 = null;
					if (chomboMesh.dimension == 3)
					{
						z0 = (double[])vectValues.get(++ c);
					}
					double[] x1 = (double[])vectValues.get(++ c);
					double[] y1 = (double[])vectValues.get(++ c);
					double[] z1 = null;
					if (chomboMesh.dimension == 3)
					{
						z1 = (double[])vectValues.get(++ c);
					}
					double[] x2 = (double[])vectValues.get(++ c);
					double[] y2 = (double[])vectValues.get(++ c);
					double[] z2 = null;
					if (chomboMesh.dimension == 3)
					{
						z2 = (double[])vectValues.get(++ c);
					}
					for (int n = 0; n < index.length; ++ n)
					{
						int memIndex = index[n];
						List<SurfaceTriangleEntry3D> edgeList = chomboMesh.surfaceTriangleMap.get(memIndex);
						if (edgeList == null)
						{
							edgeList = new ArrayList<SurfaceTriangleEntry3D>();
							chomboMesh.surfaceTriangleMap.put(index[n], edgeList);
						}
						SurfaceTriangleEntry3D mp = new SurfaceTriangleEntry3D();
						mp.memIndex = index[n];
						mp.faceIndex = faceIndex[n];
						mp.p0 = new Coordinate(x0[n], y0[n], z0 == null ? 0 : z0[n]);
						mp.p1 = new Coordinate(x1[n], y1[n], z1 == null ? 0 : z1[n]);
						mp.p2 = new Coordinate(x2[n], y2[n], z2 == null ? 0 : z2[n]);
						edgeList.add(mp);
					}
				}
				else if (name.equals(SLICE_VIEW_DATASET))
				{
					int c = 0;
					int[] index = (int[]) vectValues.get(c);
					double[][] coords = new double[12][index.length];
					for (int n = 0; n < 12; ++ n) {
						coords[n] = (double[])vectValues.get(++ c);
					}					
					for (int n = 0; n < index.length; ++ n) {
						SliceViewEntry sve = new SliceViewEntry();
						sve.memIndex = index[n];
						sve.crossPoints = new double[12];
						for (int m = 0; m < 12; ++ m)
						{
							sve.crossPoints[m] = coords[m][n];							
						}
						chomboMesh.sliceViewList.add(sve);
					}
				}
			}
		}
		// set neighbors to membrane elements
		if (chomboMesh.dimension == 2)
		{
			for (int i = 0; i < chomboMesh.membraneElements.length; ++ i)
			{
				MembraneElement me = chomboMesh.membraneElements[i];
				me.setConnectivity(chomboMesh.segments[i].prevNeigbhor, chomboMesh.segments[i].nextNeigbhor, -1, -1);
			}
		}
		return chomboMesh;
	}

	public int getDimension() {
		return dimension;
	}

	@Override
	public int getGeometryDimension() {
		return dimension;
	}
	
	@Override
	protected Object[] getOutputFields() throws IOException {
		Object[] objects = null;
		if (dimension == 2)
		{
			objects = new Object[] {dimension, size, origin, extent, membraneElements, vertices, segments};
		}
		else if (dimension == 3)
		{
			objects = new Object[] {dimension, size, origin, extent, membraneElements, vertices, surfaceTriangleMap, sliceViewList};
		}
		return objects;
	}

	@Override
	protected void inflate() {
		if (compressedBytes == null) {
			return;
		}

		try {
			Object objArray[] = (Object[])BeanUtils.fromCompressedSerialized(compressedBytes);
			int index = 0;
			dimension = (Integer)objArray[index];
			size = (ISize)objArray[++ index];
			origin = (Origin)objArray[++ index];
			extent = (Extent)objArray[++ index];
			membraneElements = (MembraneElement[])objArray[++ index];
			vertices = (Coordinate[])objArray[++ index];
			if (dimension == 2)
			{
				segments = (Segment2D[])objArray[++ index];
			}
			else if (dimension == 3)
			{
				surfaceTriangleMap = (Map<Integer, List<SurfaceTriangleEntry3D>>)objArray[++ index];
				sliceViewList = (List<SliceViewEntry>)objArray[++ index];
			}

			compressedBytes = null;
			
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public Map<Integer, List<SurfaceTriangleEntry3D>> getSurfaceMap() {
		return surfaceTriangleMap;
	}
	
	public Segment2D[] get2DSegments() {
		return segments;
	}

	public Coordinate[] getVertices() {
		return vertices;
	}
	
//	public List<MeshMetricsEntry> getMetrics() {
//		return metrics;
//	}
//	
	@Override
	public int getMembraneRegionIndex(int membraneIndex) {
		return 0;
	}
	
	@Override
	public int getVolumeRegionIndex(int volumeIndex) {
		return 0;
	}
	
	public int getSubVolumeFromVolumeIndex(int volIndex) {
		return 0;
	}
	
	@Override
	public Coordinate getCoordinateFromMembraneIndex(int membraneIndex) {
	    MembraneElement me = getMembraneElements()[membraneIndex];
	    return (new Coordinate(me.centroidX, me.centroidY, me.centroidZ));
	}
	
	@Override
	public boolean isChomboMesh()
	{
		return true;
	}
	
	public int findMembraneIndexFromVolumeIndex(CoordinateIndex ci)
	{
		int volIndex = getVolumeIndex(ci);
		for (MembraneElement me : membraneElements)
		{
			if (me.getInsideVolumeIndex() == volIndex)
			{
				return me.getMembraneIndex();
			}
		}
		return -1;
	}

	public int findMembraneIndexFromVolumeCoordinate(Coordinate wc) 
	{
		if (dx == null)
		{
			dx = new double[3];
			dx[0] = getExtent().getX()/getSizeX();
			dx[1] = getExtent().getY()/getSizeY();
			dx[2] = getExtent().getZ()/getSizeZ();
		}
		int i = (int)((wc.getX() - getOrigin().getX()) / dx[0]);
		int j = (int)((wc.getY() - getOrigin().getY()) / dx[1]);
		int k = dimension < 3 ? 0 : (int)((wc.getZ() - getOrigin().getZ()) / dx[2]);
		return findMembraneIndexFromVolumeIndex(new CoordinateIndex(i, j, k));
	}
}
