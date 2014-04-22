package com.turnguard.libtracker.sparql;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>GNOME Tracker - libtracker-sparql java bindings</h1>
 * <ul>
 * <li>introduction:
 * <ul>
 * <li>Java bindings for <a href="https://wiki.gnome.org/Projects/Tracker" target="_blank">GNOME Tracker Project</a>'s 
 * <a href="https://developer.gnome.org/libtracker-sparql/stable/" target="_blank">libtracker-sparql</a>.</li>
 * </ul>
 * </li>
 * <li>content:
 * <ul>
 * <li>Native library instance</li>
 * <li>JNA Utilities</li>
 * <li>Tracker Exception</li>
 * <li>Tracker ValueTypes enum</li>
 * <li>Tracker Structures</li>
 * <li>Tracker library</li>
 * <li>Tracker library wrapper methods</li>
 * </ul>
 * </li>
 * <li>sample usage:<br/>
 * <pre>
 * public static void main(String[] args){
 *   TrackerSparqlConnection con = null;
 *   TrackerSparqlCursor cursor = null;
 *   int countColumns = 0;
 *   try {
 *     con = Libtracker.getTrackerSparqlConnection();
 *     cursor = con.query("SELECT * WHERE { &lt;urn:sample:resource&gt; ?p ?o . }");
 *     countColumns = cursor.getColumnsCount();
 *     while(cursor.next()){
 *       for(int i = 0; i &lt; countColumns; i++){
 *         System.out.print(cursor.getString(i) + " ");
 *       }
 *       System.out.println();
 *     }
 *   } catch(TrackerException e){
 *   } finally {
 *     if(cursor!=null){
 *       try {cursor.close();} catch(Exception ee){}
 *     }
 *   }
 * }
 * </pre>
 * </li>
 * </ul>
 * @author <a href="http://www.turnguard.com/turnguard" target="_blank">http://www.turnguard.com/turnguard</a>
 */
public class Libtracker {
    /**
     * library name
     */
    public static final String LIBTRACKER_SPARQL = "tracker-sparql-1.0";    
    public static final Libtracker.Sparql SPARQL;
    public static final Map<String, Object> options = new HashMap<>();
    
    static {
        options.put(Library.OPTION_TYPE_MAPPER, new TrackerSparqlValueTypeMapper());
        SPARQL = (Libtracker.Sparql)Native.loadLibrary(LIBTRACKER_SPARQL, Libtracker.Sparql.class, options);
    };
    
    /**
     * GObject needed for JNA
     */
    public static class GObject extends PointerType {
            public GObject(Pointer address) {
                    super(address);
            }
            public GObject() {
                    super();
            }
    };   
    /**
     * GError class for Exception handling
     * @see <a href="http://searchcode.com/codesearch/view/11971074" target="_blank">http://searchcode.com/codesearch/view/11971074</a>
     */
    public static class GError extends Structure {
        public volatile int domain;
	public volatile int code;
	public volatile String message;                
        
        public GError(Pointer address) {           
            useMemory(address,0);           
        }        
        @Override
        protected List getFieldOrder() {
            return Arrays.asList("domain", "code", "message");
        }
        /**
         * Retrieve tracker's error code
         * @return int tracker's error code
         */
        public int getCode(){
            return (Integer)readField("code");
        }
        /**
         * Retrieve tracker's error message
         * @return String tracker's error message
         */
        public String getMessage(){
            return (String)readField("message");
        } 
        public static class ByReference extends GError implements Structure.ByReference {
            public ByReference(Pointer address) {
                super(address);
            }
        };
        public static class ByValue extends GError implements Structure.ByValue {
            public ByValue(Pointer address) {
                super(address);
            }
        };            
    };    
    /**
     * Interface for the mapping between java and c enum TrackerSparqlValueType
     * @param <T> 
     */
    public static interface JNAEnum<T> {
        public int getIntValue();
        public T getValue(int i);
    }
    /**
     * Converter for libtracker-sparql TrackerSparqlValueTypes
     * @see <a href="http://javarevisited.blogspot.co.at/2011/08/enum-in-java-example-tutorial.html" target="_blank">http://javarevisited.blogspot.co.at/2011/08/enum-in-java-example-tutorial.html</a>
     * @see <a href="http://technofovea.com/blog/archives/815" target="_blank">http://technofovea.com/blog/archives/815</a>
     */
    public static class EnumConverter implements TypeConverter {
        @Override
        public Object fromNative(Object input, FromNativeContext context) {
            Integer i = (Integer) input;
            Class targetClass = context.getTargetType();
            if (!JNAEnum.class.isAssignableFrom(targetClass)) {
                return null;
            }        
            Object[] enums = targetClass.getEnumConstants();
            if (enums.length == 0) {            
                return null;
            }
            JNAEnum instance = (JNAEnum) enums[0];
            return instance.getValue(i);

        }
        @Override
        public Object toNative(Object input, ToNativeContext context) {
            JNAEnum j = (JNAEnum) input;
            return new Integer(j.getIntValue());
        }
        @Override
        public Class nativeType() {
            return Integer.class;
        }
    }
    /**
     * JNA TypeMapper (added in options, when loading the native library)
     */
    public static class TrackerSparqlValueTypeMapper extends DefaultTypeMapper {
        TrackerSparqlValueTypeMapper() {
            addTypeConverter(JNAEnum.class, new EnumConverter());
        }
    };
    /**
     * Wrapper for GErrors
     */
    public static class TrackerException extends Exception {
        private int errorCode = -1;
        
        public TrackerException(GError error) {
            super(error.getMessage());
            this.errorCode = error.getCode();
        }

        public int getErrorCode() {
            return errorCode;
        }
        
    };
    /**
     * Enum for rdf-term types
     */
    public enum TrackerSparqlValueType implements JNAEnum<TrackerSparqlValueType> {
        
        TRACKER_SPARQL_VALUE_TYPE_UNBOUND(0),
        TRACKER_SPARQL_VALUE_TYPE_URI(1),
        TRACKER_SPARQL_VALUE_TYPE_STRING(2),
        TRACKER_SPARQL_VALUE_TYPE_INTEGER(3),
        TRACKER_SPARQL_VALUE_TYPE_DOUBLE(4),
        TRACKER_SPARQL_VALUE_TYPE_DATETIME(5),
        TRACKER_SPARQL_VALUE_TYPE_BLANK_NODE(6),
        TRACKER_SPARQL_VALUE_TYPE_BOOLEAN(7);
               
        private int intValue;
        private TrackerSparqlValueType(int intValue){
            this.intValue = intValue;
        }
        @Override
        public int getIntValue() {
            return intValue;
        }
        @Override
        public TrackerSparqlValueType getValue(int i){            
            for(TrackerSparqlValueType type : TrackerSparqlValueType.values()){
                if(type.ordinal()==i){
                    return type;
                }
            }
            throw new IllegalArgumentException("No Enum Constants with this intValue");
        }
    };
    
    /**
     * The TrackerSparqlConnection
     */
    public static class TrackerSparqlConnection extends Structure {       
	public Libtracker.GObject parent_instance;
	public PointerByReference priv;        
        public TrackerSparqlConnection() {}
        public TrackerSparqlConnection(TypeMapper mapper) { super(mapper); }
	public TrackerSparqlConnection(Libtracker.GObject parent_instance, PointerByReference priv) {
		super();
		this.parent_instance = parent_instance;
		this.priv = priv;
	}           
	public static class ByReference extends TrackerSparqlConnection implements Structure.ByReference {};
	public static class ByValue extends TrackerSparqlConnection implements Structure.ByValue {}; 
        @Override
	protected List<? > getFieldOrder() {
		return Arrays.asList("parent_instance", "priv");
	}        
        /**
         * Wrapper for library method tracker_sparql_connection_query
         * @param query a SPARQL 1.1 SelectQueryString
         * @return Libtracker.TrackerSparqlCursor
         * @throws com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker.TrackerException
         */
        public Libtracker.TrackerSparqlCursor query(String query) throws TrackerException{
            PointerByReference error = new PointerByReference(null);
            Libtracker.TrackerSparqlCursor cursor = Libtracker.SPARQL.tracker_sparql_connection_query(this, query, false, error);
            if(error.getValue()!=null){
                throw new TrackerException(new GError(error.getValue()));
            }
            return cursor;
        }
        /**
         * Wrapper for library method tracker_sparql_connection_update
         * @param query a SPARQL 1.1 UpdateQueryString
         * @throws com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker.TrackerException 
         */
        public void update(String query) throws TrackerException {            
            PointerByReference error = new PointerByReference(null);
            Libtracker.SPARQL.tracker_sparql_connection_update(this, query, -100, false, error);
            if(error.getValue()!=null){
                throw new TrackerException(new GError(error.getValue()));
            }
        }
        /**
         * Wrapper for library method tracker_sparql_connection_statistics
         * @return Libtracker.TrackerSparqlCursor
         * @throws com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker.TrackerException 
         */
        public Libtracker.TrackerSparqlCursor getStatistics() throws TrackerException{
            PointerByReference error = new PointerByReference(null);
            Libtracker.TrackerSparqlCursor cursor = Libtracker.SPARQL.tracker_sparql_connection_statistics(this, false, error);
            if(error.getValue()!=null){
                throw new TrackerException(new GError(error.getValue()));
            }
            return cursor;            
        }
        
    };
    
    /**
     * The TrackerSparqlCursor
     */
    public static class TrackerSparqlCursor extends Structure {	
	public Libtracker.GObject parent_instance;
	public PointerByReference priv;        
        public TrackerSparqlCursor() {}
        public TrackerSparqlCursor(TypeMapper mapper) { super(mapper); }
	public TrackerSparqlCursor(Libtracker.GObject parent_instance, PointerByReference priv) {
		super();
		this.parent_instance = parent_instance;
		this.priv = priv;
	}        
	public static class ByReference extends TrackerSparqlCursor implements Structure.ByReference {};
	public static class ByValue extends TrackerSparqlCursor implements Structure.ByValue {};
        @Override
	protected List<? > getFieldOrder() {
		return Arrays.asList("parent_instance", "priv");
	}         
        /**
         * Wrapper for library method tracker_sparql_cursor_next
         * @return boolean 
         * @throws com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker.TrackerException 
         */
        public boolean next() throws TrackerException{
            PointerByReference error = new PointerByReference(null);
            boolean b = Libtracker.SPARQL.tracker_sparql_cursor_next(this, false, error);
            if(error.getValue()!=null){
                throw new TrackerException(new GError(error.getValue()));
            }
            return b; 
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_rewind
         */
        public void rewind(){
            Libtracker.SPARQL.tracker_sparql_cursor_rewind(this);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_close
         */
        public void close(){
            Libtracker.SPARQL.tracker_sparql_cursor_close(this);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_n_columns
         * @return int the number of columns
         */
        public int getColumnsCount(){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_n_columns(this);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_is_bound
         * @param column
         * @return boolean
         */
        public boolean isBound(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_is_bound(this, column);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_string
         * @param column
         * @return String
         */
        public String getString(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_string(this, column, null);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_integer
         * @param column
         * @return int
         */
        public int getInteger(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_integer(this, column);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_double
         * @param column
         * @return double
         */
        public double getDouble(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_double(this, column);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_boolean
         * @param column
         * @return boolean
         */
        public boolean getBoolean(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_boolean(this, column);
        }  
        /**
         * Wrapper for library method tracker_sparql_cursor_get_variable_name
         * @param column
         * @return String
         */
        public String getBindingName(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_variable_name(this, column);
        }
        /**
         * Wrapper for library method tracker_sparql_cursor_get_value_type
         * @param column
         * @return Libtracker.TrackerSparqlValueType
         */
        public Libtracker.TrackerSparqlValueType getValueType(int column){
            return Libtracker.SPARQL.tracker_sparql_cursor_get_value_type(this, column);
        }
    };    
    /**
     * The libtracker-sparql bindings
     */
    public interface Sparql extends Library {
        /**
         * Return a simple urn:uuid
         * @return String
         */
        public String tracker_sparql_get_uuid_urn();                
        /**
         * Binding for: TrackerSparqlConnection* tracker_sparql_connection_get (GCancellable* cancellable, GError** error);
         * @param canceable
         * @param error
         * @return Libtracker.TrackerSparqlConnection
         */
        public Libtracker.TrackerSparqlConnection tracker_sparql_connection_get(boolean canceable, PointerByReference error);
        /**
         * Binding for: TrackerSparqlCursor* tracker_sparql_connection_query (TrackerSparqlConnection* self, const gchar* sparql, GCancellable* cancellable, GError** error);
         * @param con
         * @param query
         * @param cancellable
         * @param error
         * @return Libtracker.TrackerSparqlCursor
         */
        public Libtracker.TrackerSparqlCursor tracker_sparql_connection_query(Libtracker.TrackerSparqlConnection con, String query, boolean cancellable, PointerByReference error);
        /**
         * Binding for: void tracker_sparql_connection_update (TrackerSparqlConnection* self, const gchar* sparql, gint priority, GCancellable* cancellable, GError** error);
         * @param con
         * @param query
         * @param glibPriority
         * @param cancellable
         * @param error 
         */
        public void tracker_sparql_connection_update(Libtracker.TrackerSparqlConnection con, String query, int glibPriority, boolean cancellable, PointerByReference error);
        /**
         * Binding for: gboolean tracker_sparql_cursor_next (TrackerSparqlCursor* self, GCancellable* cancellable, GError** error);
         * @param cursor
         * @param canceable
         * @param error
         * @return boolean
         */
        public boolean tracker_sparql_cursor_next(Libtracker.TrackerSparqlCursor cursor, boolean canceable, PointerByReference error);
        /**
         * Binding for: void tracker_sparql_cursor_rewind (TrackerSparqlCursor* self);
         * @param cursor 
         */
        public void tracker_sparql_cursor_rewind (Libtracker.TrackerSparqlCursor cursor);
        /**
         * Binding for: gint tracker_sparql_cursor_get_n_columns (TrackerSparqlCursor* self);
         * @param cursor
         * @return int
         */
        public int tracker_sparql_cursor_get_n_columns(Libtracker.TrackerSparqlCursor cursor);    
        /**
         * Binding for: const gchar* tracker_sparql_cursor_get_variable_name (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return String
         */
        public String tracker_sparql_cursor_get_variable_name(TrackerSparqlCursor cursor, int column);
        /**
         * Binding for: gboolean tracker_sparql_cursor_is_bound (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return boolean
         */
        public boolean tracker_sparql_cursor_is_bound (Libtracker.TrackerSparqlCursor cursor, int column);
        /**
         * Binding for: TrackerSparqlValueType tracker_sparql_cursor_get_value_type (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return TrackerSparqlValueType
         */
        public TrackerSparqlValueType tracker_sparql_cursor_get_value_type (TrackerSparqlCursor cursor, int column);
        /**
         * Binding for: const gchar* tracker_sparql_cursor_get_string (TrackerSparqlCursor* self, gint column, glong* length);
         * @param cursor
         * @param column
         * @param offset
         * @return String
         */
        public String tracker_sparql_cursor_get_string(Libtracker.TrackerSparqlCursor cursor, int column, Long offset);
        /**
         * Binding for: gint64 tracker_sparql_cursor_get_integer (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return int
         */
        public int tracker_sparql_cursor_get_integer(Libtracker.TrackerSparqlCursor cursor, int column);
        /**
         * Binding for: gdouble tracker_sparql_cursor_get_double (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return double
         */
        public double tracker_sparql_cursor_get_double (Libtracker.TrackerSparqlCursor cursor, int column);
        /**
         * Binding for: gboolean tracker_sparql_cursor_get_boolean (TrackerSparqlCursor* self, gint column);
         * @param cursor
         * @param column
         * @return boolean
         */
        public boolean tracker_sparql_cursor_get_boolean (Libtracker.TrackerSparqlCursor cursor, int column);        
        /**
         * Binding for: void tracker_sparql_cursor_close (TrackerSparqlCursor* self);
         * @param cursor 
         */
        public void tracker_sparql_cursor_close(Libtracker.TrackerSparqlCursor cursor);
        /**
         * Binding for: TrackerSparqlCursor* tracker_sparql_connection_statistics (TrackerSparqlConnection* self, GCancellable* cancellable, GError** error);
         * @param con
         * @param canceable
         * @param error
         * @return Libtracker.TrackerSparqlCursor
         */
        public Libtracker.TrackerSparqlCursor tracker_sparql_connection_statistics(Libtracker.TrackerSparqlConnection con, boolean canceable, PointerByReference error);
    }
    
    /**
     * Wrapper for library method tracker_sparql_connection_get
     * @return Libtracker.TrackerSparqlConnection
     * @throws com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker.TrackerException 
     */
    public static Libtracker.TrackerSparqlConnection getTrackerSparqlConnection() throws TrackerException{
        PointerByReference error = new PointerByReference(null);
        Libtracker.TrackerSparqlConnection con = Libtracker.SPARQL.tracker_sparql_connection_get(false, error);
        if(error.getValue()!=null){
            throw new TrackerException(new GError(error.getValue()));
        }
        return con;
    }
}
