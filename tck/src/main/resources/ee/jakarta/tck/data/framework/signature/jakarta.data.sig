#Signature file v4.1
#Version 1.0.0-SNAPSHOT

CLSS public jakarta.data.DataException
cons public init(java.lang.String)
cons public init(java.lang.String,java.lang.Throwable)
cons public init(java.lang.Throwable)
supr java.lang.RuntimeException

CLSS public abstract interface !annotation jakarta.data.Entity
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[TYPE])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault java.lang.String value()

CLSS public abstract interface !annotation jakarta.data.Id
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[FIELD])
intf java.lang.annotation.Annotation
meth public abstract !hasdefault java.lang.String value()

CLSS abstract interface jakarta.data.package-info

CLSS public abstract interface jakarta.data.repository.CrudRepository<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf jakarta.data.repository.DataRepository<{jakarta.data.repository.CrudRepository%0},{jakarta.data.repository.CrudRepository%1}>
meth public abstract <%0 extends {jakarta.data.repository.CrudRepository%0}> java.lang.Iterable<{%%0}> saveAll(java.lang.Iterable<{%%0}>)
meth public abstract <%0 extends {jakarta.data.repository.CrudRepository%0}> {%%0} save({%%0})
meth public abstract boolean existsById({jakarta.data.repository.CrudRepository%1})
meth public abstract java.util.Optional<{jakarta.data.repository.CrudRepository%0}> findById({jakarta.data.repository.CrudRepository%1})
meth public abstract java.util.stream.Stream<{jakarta.data.repository.CrudRepository%0}> findAll()
meth public abstract java.util.stream.Stream<{jakarta.data.repository.CrudRepository%0}> findAllById(java.lang.Iterable<{jakarta.data.repository.CrudRepository%1}>)
meth public abstract long count()
meth public abstract void delete({jakarta.data.repository.CrudRepository%0})
meth public abstract void deleteAll()
meth public abstract void deleteAll(java.lang.Iterable<? extends {jakarta.data.repository.CrudRepository%0}>)
meth public abstract void deleteAllById(java.lang.Iterable<{jakarta.data.repository.CrudRepository%1}>)
meth public abstract void deleteById({jakarta.data.repository.CrudRepository%1})

CLSS public abstract interface jakarta.data.repository.DataRepository<%0 extends java.lang.Object, %1 extends java.lang.Object>

CLSS public final !enum jakarta.data.repository.Direction
fld public final static jakarta.data.repository.Direction ASC
fld public final static jakarta.data.repository.Direction DESC
meth public static jakarta.data.repository.Direction valueOf(java.lang.String)
meth public static jakarta.data.repository.Direction[] values()
supr java.lang.Enum<jakarta.data.repository.Direction>

CLSS public jakarta.data.repository.Limit
meth public long maxResults()
meth public long startAt()
meth public static jakarta.data.repository.Limit of(long)
meth public static jakarta.data.repository.Limit range(long,long)
supr java.lang.Object
hfds DEFAULT_START_AT,maxResults,startAt

CLSS public abstract interface !annotation jakarta.data.repository.OrderBy
 anno 0 java.lang.annotation.Repeatable(java.lang.Class<? extends java.lang.annotation.Annotation> value=class jakarta.data.repository.OrderBy$List)
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD])
innr public abstract interface static !annotation List
intf java.lang.annotation.Annotation
meth public abstract !hasdefault boolean descending()
meth public abstract java.lang.String value()

CLSS public abstract interface static !annotation jakarta.data.repository.OrderBy$List
 outer jakarta.data.repository.OrderBy
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD])
intf java.lang.annotation.Annotation
meth public abstract jakarta.data.repository.OrderBy[] value()

CLSS public jakarta.data.repository.Pageable
meth public boolean equals(java.lang.Object)
meth public int hashCode()
meth public jakarta.data.repository.Pageable next()
meth public java.lang.String toString()
meth public long getPage()
meth public long getSize()
meth public static jakarta.data.repository.Pageable of(long,long)
meth public static jakarta.data.repository.Pageable page(long)
meth public static jakarta.data.repository.Pageable size(long)
supr java.lang.Object
hfds DEFAULT_SIZE,page,size

CLSS public abstract interface jakarta.data.repository.PageableRepository<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf jakarta.data.repository.CrudRepository<{jakarta.data.repository.PageableRepository%0},{jakarta.data.repository.PageableRepository%1}>

CLSS public abstract interface !annotation jakarta.data.repository.Param
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[PARAMETER])
intf java.lang.annotation.Annotation
meth public abstract java.lang.String value()

CLSS public abstract interface !annotation jakarta.data.repository.Query
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[METHOD])
intf java.lang.annotation.Annotation
meth public abstract java.lang.String value()

CLSS public abstract interface jakarta.data.repository.ReactiveRepository<%0 extends java.lang.Object, %1 extends java.lang.Object>
intf jakarta.data.repository.DataRepository<{jakarta.data.repository.ReactiveRepository%0},{jakarta.data.repository.ReactiveRepository%1}>

CLSS public abstract interface !annotation jakarta.data.repository.Repository
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[TYPE])
intf java.lang.annotation.Annotation

CLSS public jakarta.data.repository.Sort
meth public boolean equals(java.lang.Object)
meth public boolean isAscending()
meth public boolean isDescending()
meth public int hashCode()
meth public java.lang.String getProperty()
meth public java.lang.String toString()
meth public static jakarta.data.repository.Sort asc(java.lang.String)
meth public static jakarta.data.repository.Sort desc(java.lang.String)
meth public static jakarta.data.repository.Sort of(java.lang.String,jakarta.data.repository.Direction)
supr java.lang.Object
hfds direction,property

CLSS abstract interface jakarta.data.repository.package-info

CLSS public abstract interface java.io.Serializable

CLSS public abstract interface java.lang.Comparable<%0 extends java.lang.Object>
meth public abstract int compareTo({java.lang.Comparable%0})

CLSS public abstract java.lang.Enum<%0 extends java.lang.Enum<{java.lang.Enum%0}>>
cons protected init(java.lang.String,int)
intf java.io.Serializable
intf java.lang.Comparable<{java.lang.Enum%0}>
meth protected final java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected final void finalize()
meth public final boolean equals(java.lang.Object)
meth public final int compareTo({java.lang.Enum%0})
meth public final int hashCode()
meth public final int ordinal()
meth public final java.lang.Class<{java.lang.Enum%0}> getDeclaringClass()
meth public final java.lang.String name()
meth public java.lang.String toString()
meth public static <%0 extends java.lang.Enum<{%%0}>> {%%0} valueOf(java.lang.Class<{%%0}>,java.lang.String)
supr java.lang.Object

CLSS public java.lang.Exception
cons protected init(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public init()
cons public init(java.lang.String)
cons public init(java.lang.String,java.lang.Throwable)
cons public init(java.lang.Throwable)
supr java.lang.Throwable

CLSS public java.lang.Object
cons public init()
meth protected java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected void finalize() throws java.lang.Throwable
 anno 0 java.lang.Deprecated(boolean forRemoval=false, java.lang.String since="9")
meth public boolean equals(java.lang.Object)
meth public final java.lang.Class<?> getClass()
meth public final void notify()
meth public final void notifyAll()
meth public final void wait() throws java.lang.InterruptedException
meth public final void wait(long) throws java.lang.InterruptedException
meth public final void wait(long,int) throws java.lang.InterruptedException
meth public int hashCode()
meth public java.lang.String toString()

CLSS public java.lang.RuntimeException
cons protected init(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public init()
cons public init(java.lang.String)
cons public init(java.lang.String,java.lang.Throwable)
cons public init(java.lang.Throwable)
supr java.lang.Exception

CLSS public java.lang.Throwable
cons protected init(java.lang.String,java.lang.Throwable,boolean,boolean)
cons public init()
cons public init(java.lang.String)
cons public init(java.lang.String,java.lang.Throwable)
cons public init(java.lang.Throwable)
intf java.io.Serializable
meth public final java.lang.Throwable[] getSuppressed()
meth public final void addSuppressed(java.lang.Throwable)
meth public java.lang.StackTraceElement[] getStackTrace()
meth public java.lang.String getLocalizedMessage()
meth public java.lang.String getMessage()
meth public java.lang.String toString()
meth public java.lang.Throwable fillInStackTrace()
meth public java.lang.Throwable getCause()
meth public java.lang.Throwable initCause(java.lang.Throwable)
meth public void printStackTrace()
meth public void printStackTrace(java.io.PrintStream)
meth public void printStackTrace(java.io.PrintWriter)
meth public void setStackTrace(java.lang.StackTraceElement[])
supr java.lang.Object

CLSS public abstract interface java.lang.annotation.Annotation
meth public abstract boolean equals(java.lang.Object)
meth public abstract int hashCode()
meth public abstract java.lang.Class<? extends java.lang.annotation.Annotation> annotationType()
meth public abstract java.lang.String toString()

CLSS public abstract interface !annotation java.lang.annotation.Documented
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation

CLSS public abstract interface !annotation java.lang.annotation.Repeatable
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.Class<? extends java.lang.annotation.Annotation> value()

CLSS public abstract interface !annotation java.lang.annotation.Retention
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.RetentionPolicy value()

CLSS public abstract interface !annotation java.lang.annotation.Target
 anno 0 java.lang.annotation.Documented()
 anno 0 java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy value=RUNTIME)
 anno 0 java.lang.annotation.Target(java.lang.annotation.ElementType[] value=[ANNOTATION_TYPE])
intf java.lang.annotation.Annotation
meth public abstract java.lang.annotation.ElementType[] value()

