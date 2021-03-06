/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.vcell.imagedataset;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)")
public class ImageSizeInfo implements org.apache.thrift.TBase<ImageSizeInfo, ImageSizeInfo._Fields>, java.io.Serializable, Cloneable, Comparable<ImageSizeInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ImageSizeInfo");

  private static final org.apache.thrift.protocol.TField IMAGE_PATH_FIELD_DESC = new org.apache.thrift.protocol.TField("imagePath", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField I_SIZE_FIELD_DESC = new org.apache.thrift.protocol.TField("iSize", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField NUM_CHANNELS_FIELD_DESC = new org.apache.thrift.protocol.TField("numChannels", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField TIME_POINTS_FIELD_DESC = new org.apache.thrift.protocol.TField("timePoints", org.apache.thrift.protocol.TType.LIST, (short)4);
  private static final org.apache.thrift.protocol.TField SELECTED_TIME_INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("selectedTimeIndex", org.apache.thrift.protocol.TType.I32, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ImageSizeInfoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ImageSizeInfoTupleSchemeFactory();

  public java.lang.String imagePath; // required
  public ISize iSize; // required
  public int numChannels; // required
  public java.util.List<java.lang.Double> timePoints; // required
  public int selectedTimeIndex; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    IMAGE_PATH((short)1, "imagePath"),
    I_SIZE((short)2, "iSize"),
    NUM_CHANNELS((short)3, "numChannels"),
    TIME_POINTS((short)4, "timePoints"),
    SELECTED_TIME_INDEX((short)5, "selectedTimeIndex");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // IMAGE_PATH
          return IMAGE_PATH;
        case 2: // I_SIZE
          return I_SIZE;
        case 3: // NUM_CHANNELS
          return NUM_CHANNELS;
        case 4: // TIME_POINTS
          return TIME_POINTS;
        case 5: // SELECTED_TIME_INDEX
          return SELECTED_TIME_INDEX;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __NUMCHANNELS_ISSET_ID = 0;
  private static final int __SELECTEDTIMEINDEX_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.IMAGE_PATH, new org.apache.thrift.meta_data.FieldMetaData("imagePath", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_SIZE, new org.apache.thrift.meta_data.FieldMetaData("iSize", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ISize.class)));
    tmpMap.put(_Fields.NUM_CHANNELS, new org.apache.thrift.meta_data.FieldMetaData("numChannels", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.TIME_POINTS, new org.apache.thrift.meta_data.FieldMetaData("timePoints", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    tmpMap.put(_Fields.SELECTED_TIME_INDEX, new org.apache.thrift.meta_data.FieldMetaData("selectedTimeIndex", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ImageSizeInfo.class, metaDataMap);
  }

  public ImageSizeInfo() {
  }

  public ImageSizeInfo(
    java.lang.String imagePath,
    ISize iSize,
    int numChannels,
    java.util.List<java.lang.Double> timePoints,
    int selectedTimeIndex)
  {
    this();
    this.imagePath = imagePath;
    this.iSize = iSize;
    this.numChannels = numChannels;
    setNumChannelsIsSet(true);
    this.timePoints = timePoints;
    this.selectedTimeIndex = selectedTimeIndex;
    setSelectedTimeIndexIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ImageSizeInfo(ImageSizeInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetImagePath()) {
      this.imagePath = other.imagePath;
    }
    if (other.isSetISize()) {
      this.iSize = new ISize(other.iSize);
    }
    this.numChannels = other.numChannels;
    if (other.isSetTimePoints()) {
      java.util.List<java.lang.Double> __this__timePoints = new java.util.ArrayList<java.lang.Double>(other.timePoints);
      this.timePoints = __this__timePoints;
    }
    this.selectedTimeIndex = other.selectedTimeIndex;
  }

  public ImageSizeInfo deepCopy() {
    return new ImageSizeInfo(this);
  }

  @Override
  public void clear() {
    this.imagePath = null;
    this.iSize = null;
    setNumChannelsIsSet(false);
    this.numChannels = 0;
    this.timePoints = null;
    setSelectedTimeIndexIsSet(false);
    this.selectedTimeIndex = 0;
  }

  public java.lang.String getImagePath() {
    return this.imagePath;
  }

  public ImageSizeInfo setImagePath(java.lang.String imagePath) {
    this.imagePath = imagePath;
    return this;
  }

  public void unsetImagePath() {
    this.imagePath = null;
  }

  /** Returns true if field imagePath is set (has been assigned a value) and false otherwise */
  public boolean isSetImagePath() {
    return this.imagePath != null;
  }

  public void setImagePathIsSet(boolean value) {
    if (!value) {
      this.imagePath = null;
    }
  }

  public ISize getISize() {
    return this.iSize;
  }

  public ImageSizeInfo setISize(ISize iSize) {
    this.iSize = iSize;
    return this;
  }

  public void unsetISize() {
    this.iSize = null;
  }

  /** Returns true if field iSize is set (has been assigned a value) and false otherwise */
  public boolean isSetISize() {
    return this.iSize != null;
  }

  public void setISizeIsSet(boolean value) {
    if (!value) {
      this.iSize = null;
    }
  }

  public int getNumChannels() {
    return this.numChannels;
  }

  public ImageSizeInfo setNumChannels(int numChannels) {
    this.numChannels = numChannels;
    setNumChannelsIsSet(true);
    return this;
  }

  public void unsetNumChannels() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __NUMCHANNELS_ISSET_ID);
  }

  /** Returns true if field numChannels is set (has been assigned a value) and false otherwise */
  public boolean isSetNumChannels() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __NUMCHANNELS_ISSET_ID);
  }

  public void setNumChannelsIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __NUMCHANNELS_ISSET_ID, value);
  }

  public int getTimePointsSize() {
    return (this.timePoints == null) ? 0 : this.timePoints.size();
  }

  public java.util.Iterator<java.lang.Double> getTimePointsIterator() {
    return (this.timePoints == null) ? null : this.timePoints.iterator();
  }

  public void addToTimePoints(double elem) {
    if (this.timePoints == null) {
      this.timePoints = new java.util.ArrayList<java.lang.Double>();
    }
    this.timePoints.add(elem);
  }

  public java.util.List<java.lang.Double> getTimePoints() {
    return this.timePoints;
  }

  public ImageSizeInfo setTimePoints(java.util.List<java.lang.Double> timePoints) {
    this.timePoints = timePoints;
    return this;
  }

  public void unsetTimePoints() {
    this.timePoints = null;
  }

  /** Returns true if field timePoints is set (has been assigned a value) and false otherwise */
  public boolean isSetTimePoints() {
    return this.timePoints != null;
  }

  public void setTimePointsIsSet(boolean value) {
    if (!value) {
      this.timePoints = null;
    }
  }

  public int getSelectedTimeIndex() {
    return this.selectedTimeIndex;
  }

  public ImageSizeInfo setSelectedTimeIndex(int selectedTimeIndex) {
    this.selectedTimeIndex = selectedTimeIndex;
    setSelectedTimeIndexIsSet(true);
    return this;
  }

  public void unsetSelectedTimeIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SELECTEDTIMEINDEX_ISSET_ID);
  }

  /** Returns true if field selectedTimeIndex is set (has been assigned a value) and false otherwise */
  public boolean isSetSelectedTimeIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SELECTEDTIMEINDEX_ISSET_ID);
  }

  public void setSelectedTimeIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SELECTEDTIMEINDEX_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case IMAGE_PATH:
      if (value == null) {
        unsetImagePath();
      } else {
        setImagePath((java.lang.String)value);
      }
      break;

    case I_SIZE:
      if (value == null) {
        unsetISize();
      } else {
        setISize((ISize)value);
      }
      break;

    case NUM_CHANNELS:
      if (value == null) {
        unsetNumChannels();
      } else {
        setNumChannels((java.lang.Integer)value);
      }
      break;

    case TIME_POINTS:
      if (value == null) {
        unsetTimePoints();
      } else {
        setTimePoints((java.util.List<java.lang.Double>)value);
      }
      break;

    case SELECTED_TIME_INDEX:
      if (value == null) {
        unsetSelectedTimeIndex();
      } else {
        setSelectedTimeIndex((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case IMAGE_PATH:
      return getImagePath();

    case I_SIZE:
      return getISize();

    case NUM_CHANNELS:
      return getNumChannels();

    case TIME_POINTS:
      return getTimePoints();

    case SELECTED_TIME_INDEX:
      return getSelectedTimeIndex();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case IMAGE_PATH:
      return isSetImagePath();
    case I_SIZE:
      return isSetISize();
    case NUM_CHANNELS:
      return isSetNumChannels();
    case TIME_POINTS:
      return isSetTimePoints();
    case SELECTED_TIME_INDEX:
      return isSetSelectedTimeIndex();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof ImageSizeInfo)
      return this.equals((ImageSizeInfo)that);
    return false;
  }

  public boolean equals(ImageSizeInfo that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_imagePath = true && this.isSetImagePath();
    boolean that_present_imagePath = true && that.isSetImagePath();
    if (this_present_imagePath || that_present_imagePath) {
      if (!(this_present_imagePath && that_present_imagePath))
        return false;
      if (!this.imagePath.equals(that.imagePath))
        return false;
    }

    boolean this_present_iSize = true && this.isSetISize();
    boolean that_present_iSize = true && that.isSetISize();
    if (this_present_iSize || that_present_iSize) {
      if (!(this_present_iSize && that_present_iSize))
        return false;
      if (!this.iSize.equals(that.iSize))
        return false;
    }

    boolean this_present_numChannels = true;
    boolean that_present_numChannels = true;
    if (this_present_numChannels || that_present_numChannels) {
      if (!(this_present_numChannels && that_present_numChannels))
        return false;
      if (this.numChannels != that.numChannels)
        return false;
    }

    boolean this_present_timePoints = true && this.isSetTimePoints();
    boolean that_present_timePoints = true && that.isSetTimePoints();
    if (this_present_timePoints || that_present_timePoints) {
      if (!(this_present_timePoints && that_present_timePoints))
        return false;
      if (!this.timePoints.equals(that.timePoints))
        return false;
    }

    boolean this_present_selectedTimeIndex = true;
    boolean that_present_selectedTimeIndex = true;
    if (this_present_selectedTimeIndex || that_present_selectedTimeIndex) {
      if (!(this_present_selectedTimeIndex && that_present_selectedTimeIndex))
        return false;
      if (this.selectedTimeIndex != that.selectedTimeIndex)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetImagePath()) ? 131071 : 524287);
    if (isSetImagePath())
      hashCode = hashCode * 8191 + imagePath.hashCode();

    hashCode = hashCode * 8191 + ((isSetISize()) ? 131071 : 524287);
    if (isSetISize())
      hashCode = hashCode * 8191 + iSize.hashCode();

    hashCode = hashCode * 8191 + numChannels;

    hashCode = hashCode * 8191 + ((isSetTimePoints()) ? 131071 : 524287);
    if (isSetTimePoints())
      hashCode = hashCode * 8191 + timePoints.hashCode();

    hashCode = hashCode * 8191 + selectedTimeIndex;

    return hashCode;
  }

  @Override
  public int compareTo(ImageSizeInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetImagePath()).compareTo(other.isSetImagePath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetImagePath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.imagePath, other.imagePath);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetISize()).compareTo(other.isSetISize());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetISize()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iSize, other.iSize);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetNumChannels()).compareTo(other.isSetNumChannels());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumChannels()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numChannels, other.numChannels);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTimePoints()).compareTo(other.isSetTimePoints());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimePoints()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timePoints, other.timePoints);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSelectedTimeIndex()).compareTo(other.isSetSelectedTimeIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSelectedTimeIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.selectedTimeIndex, other.selectedTimeIndex);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("ImageSizeInfo(");
    boolean first = true;

    sb.append("imagePath:");
    if (this.imagePath == null) {
      sb.append("null");
    } else {
      sb.append(this.imagePath);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iSize:");
    if (this.iSize == null) {
      sb.append("null");
    } else {
      sb.append(this.iSize);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("numChannels:");
    sb.append(this.numChannels);
    first = false;
    if (!first) sb.append(", ");
    sb.append("timePoints:");
    if (this.timePoints == null) {
      sb.append("null");
    } else {
      sb.append(this.timePoints);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("selectedTimeIndex:");
    sb.append(this.selectedTimeIndex);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (imagePath == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'imagePath' was not present! Struct: " + toString());
    }
    if (iSize == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iSize' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'numChannels' because it's a primitive and you chose the non-beans generator.
    if (timePoints == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'timePoints' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'selectedTimeIndex' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
    if (iSize != null) {
      iSize.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ImageSizeInfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ImageSizeInfoStandardScheme getScheme() {
      return new ImageSizeInfoStandardScheme();
    }
  }

  private static class ImageSizeInfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<ImageSizeInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ImageSizeInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // IMAGE_PATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.imagePath = iprot.readString();
              struct.setImagePathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // I_SIZE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.iSize = new ISize();
              struct.iSize.read(iprot);
              struct.setISizeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NUM_CHANNELS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numChannels = iprot.readI32();
              struct.setNumChannelsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TIME_POINTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.timePoints = new java.util.ArrayList<java.lang.Double>(_list0.size);
                double _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readDouble();
                  struct.timePoints.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setTimePointsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // SELECTED_TIME_INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.selectedTimeIndex = iprot.readI32();
              struct.setSelectedTimeIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetNumChannels()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'numChannels' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetSelectedTimeIndex()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'selectedTimeIndex' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ImageSizeInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.imagePath != null) {
        oprot.writeFieldBegin(IMAGE_PATH_FIELD_DESC);
        oprot.writeString(struct.imagePath);
        oprot.writeFieldEnd();
      }
      if (struct.iSize != null) {
        oprot.writeFieldBegin(I_SIZE_FIELD_DESC);
        struct.iSize.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(NUM_CHANNELS_FIELD_DESC);
      oprot.writeI32(struct.numChannels);
      oprot.writeFieldEnd();
      if (struct.timePoints != null) {
        oprot.writeFieldBegin(TIME_POINTS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, struct.timePoints.size()));
          for (double _iter3 : struct.timePoints)
          {
            oprot.writeDouble(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SELECTED_TIME_INDEX_FIELD_DESC);
      oprot.writeI32(struct.selectedTimeIndex);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ImageSizeInfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ImageSizeInfoTupleScheme getScheme() {
      return new ImageSizeInfoTupleScheme();
    }
  }

  private static class ImageSizeInfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<ImageSizeInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ImageSizeInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.imagePath);
      struct.iSize.write(oprot);
      oprot.writeI32(struct.numChannels);
      {
        oprot.writeI32(struct.timePoints.size());
        for (double _iter4 : struct.timePoints)
        {
          oprot.writeDouble(_iter4);
        }
      }
      oprot.writeI32(struct.selectedTimeIndex);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ImageSizeInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.imagePath = iprot.readString();
      struct.setImagePathIsSet(true);
      struct.iSize = new ISize();
      struct.iSize.read(iprot);
      struct.setISizeIsSet(true);
      struct.numChannels = iprot.readI32();
      struct.setNumChannelsIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
        struct.timePoints = new java.util.ArrayList<java.lang.Double>(_list5.size);
        double _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readDouble();
          struct.timePoints.add(_elem6);
        }
      }
      struct.setTimePointsIsSet(true);
      struct.selectedTimeIndex = iprot.readI32();
      struct.setSelectedTimeIndexIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

