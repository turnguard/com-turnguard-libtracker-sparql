com-turnguard-libtracker-sparql
===============================

<h3>GNOME Tracker - libtracker-sparql java bindings</h3>
<ul>
<li>introduction:
<ul>
<li>Java bindings for <a href="https://wiki.gnome.org/Projects/Tracker" target="_blank">GNOME Tracker Project</a>'s 
<a href="https://developer.gnome.org/libtracker-sparql/stable/" target="_blank">libtracker-sparql</a>.</li>
</ul>
</li>
<li>requirements:
<ul>
<li>libtracker-sparql-1.0</li>
</ul>
</li>
<li>content (com.turnguard.rdf.com.turnguard.libtracker.sparql.Libtracker):
<ul>
<li>Native library instance</li>
<li>JNA Utilities</li>
<li>Tracker Exception</li>
<li>Tracker ValueTypes enum</li>
<li>Tracker Structures</li>
<li>Tracker library</li>
<li>Tracker library wrapper methods</li>
</ul>
</li>
<li>sample usage:<br/>
<pre>
public static void main(String[] args){
  TrackerSparqlConnection con = null;
  TrackerSparqlCursor cursor = null;
  int countColumns = 0;
  try {
    con = Libtracker.getTrackerSparqlConnection();
    cursor = con.query("SELECTWHERE { &lt;urn:sample:resource&gt; ?p ?o . }");
    countColumns = cursor.getColumnsCount();
    while(cursor.next()){
      for(int i = 0; i &lt; countColumns; i++){
        System.out.print(cursor.getString(i) + " ");
      }
      System.out.println();
    }
  } catch(TrackerException e){
  } finally {
    if(cursor!=null){
      try {cursor.close();} catch(Exception ee){}
    }
  }
}
</pre>
</li>
</ul>
