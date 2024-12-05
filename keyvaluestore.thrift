 /**
 * The first thing to know about are types. The available types in Thrift are:
 *
 *  bool        Boolean, one byte
 *  i8 (byte)   Signed 8-bit integer
 *  i16         Signed 16-bit integer
 *  i32         Signed 32-bit integer
 *  i64         Signed 64-bit integer
 *  double      64-bit floating point value
 *  string      String
 *  binary      Blob (byte array)
 *  map<t1,t2>  Map from one type to another
 *  list<t1>    Ordered list of one type
 *  set<t1>     Set of unique elements of one type
 *
 */
namespace java keyvaluestore

/**
 * You can define enums, which are just 32 bit integers. Values are optional
 * and start at 1 if not supplied, C style again.
 */
enum Operation {
  GET = 1,
  PUT = 2,
  DELETE = 3
}

/**
 * Structs can also be exceptions, if they are nasty.
 */
exception InvalidOperation {
  1: Operation op,
  2: string why
}

/**
 *
 */
service KeyValueStore {

  /**
   *
   */

   string put(1: string key, 2: string value) throws (1: InvalidOperation err)

   string get(1: string key) throws (1: InvalidOperation err)

   string delete(1: string key) throws (1: InvalidOperation err)

   bool preparePaxos(1: string proposalId, 2: string key, 3: string value)

   bool acceptPaxos(1: string proposalId, 2: string key, 3: string value)

   void learnPaxos(1: string key, 2: string value)
}