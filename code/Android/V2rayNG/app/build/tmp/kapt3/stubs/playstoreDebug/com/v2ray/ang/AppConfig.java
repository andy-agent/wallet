package com.v2ray.ang;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\bo\n\u0002\u0010\b\n\u0002\b5\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00106\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00107\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00108\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00109\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010:\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010;\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010=\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010?\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010@\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010A\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010B\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010C\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010D\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010E\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010F\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010G\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010H\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010I\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010J\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010K\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010L\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010M\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010N\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010O\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010P\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010Q\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010R\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010S\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010T\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010U\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010V\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010W\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010X\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010Y\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010Z\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010[\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\\\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010]\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010^\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010_\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010`\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010a\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010c\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010d\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010g\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010h\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010i\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010j\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010k\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010l\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010m\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010o\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010p\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010q\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010s\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010t\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010v\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010w\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010x\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010y\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010z\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010{\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010|\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010}\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010~\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u007f\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0080\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0081\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0082\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0083\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0084\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0085\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0086\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0087\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0088\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0089\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008a\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008b\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008c\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008d\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008e\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008f\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0090\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0091\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0092\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0093\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0094\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0095\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0096\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0097\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0098\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0099\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009a\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009b\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009c\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009d\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009e\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u009f\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a0\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a1\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a2\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a3\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a4\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a5\u0001\u001a\u00020uX\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a6\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a7\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a8\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00a9\u0001\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R&\u0010\u00aa\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00ad\u0001\u0010\u00ae\u0001R&\u0010\u00af\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00b0\u0001\u0010\u00ae\u0001R&\u0010\u00b1\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00b2\u0001\u0010\u00ae\u0001R&\u0010\u00b3\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00b4\u0001\u0010\u00ae\u0001R&\u0010\u00b5\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00b6\u0001\u0010\u00ae\u0001R&\u0010\u00b7\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00b8\u0001\u0010\u00ae\u0001R&\u0010\u00b9\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00ba\u0001\u0010\u00ae\u0001R&\u0010\u00bb\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00bc\u0001\u0010\u00ae\u0001R&\u0010\u00bd\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00be\u0001\u0010\u00ae\u0001R&\u0010\u00bf\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00c0\u0001\u0010\u00ae\u0001R&\u0010\u00c1\u0001\u001a\u0014\u0012\u0004\u0012\u00020\u00050\u00ab\u0001j\t\u0012\u0004\u0012\u00020\u0005`\u00ac\u0001\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00c2\u0001\u0010\u00ae\u0001\u00a8\u0006\u00c3\u0001"}, d2 = {"Lcom/v2ray/ang/AppConfig;", "", "<init>", "()V", "ANG_PACKAGE", "", "TAG", "DIR_ASSETS", "WEBDAV_BACKUP_DIR", "WEBDAV_BACKUP_FILE_NAME", "ANG_CONFIG", "DEFAULT_SUBSCRIPTION_ID", "PREF_SNIFFING_ENABLED", "PREF_ROUTE_ONLY_ENABLED", "PREF_PER_APP_PROXY", "PREF_PER_APP_PROXY_SET", "PREF_BYPASS_APPS", "PREF_LOCAL_DNS_ENABLED", "PREF_FAKE_DNS_ENABLED", "PREF_APPEND_HTTP_PROXY", "PREF_LOCAL_DNS_PORT", "PREF_VPN_DNS", "PREF_VPN_BYPASS_LAN", "PREF_VPN_INTERFACE_ADDRESS_CONFIG_INDEX", "PREF_VPN_MTU", "PREF_ROUTING_DOMAIN_STRATEGY", "PREF_ROUTING_RULESET", "PREF_MUX_ENABLED", "PREF_MUX_CONCURRENCY", "PREF_MUX_XUDP_CONCURRENCY", "PREF_MUX_XUDP_QUIC", "PREF_FRAGMENT_ENABLED", "PREF_FRAGMENT_PACKETS", "PREF_FRAGMENT_LENGTH", "PREF_FRAGMENT_INTERVAL", "SUBSCRIPTION_AUTO_UPDATE", "SUBSCRIPTION_AUTO_UPDATE_INTERVAL", "SUBSCRIPTION_DEFAULT_UPDATE_INTERVAL", "SUBSCRIPTION_UPDATE_TASK_NAME", "PREF_SPEED_ENABLED", "PREF_CONFIRM_REMOVE", "PREF_START_SCAN_IMMEDIATE", "PREF_DOUBLE_COLUMN_DISPLAY", "PREF_GROUP_ALL_DISPLAY", "PREF_LANGUAGE", "PREF_UI_MODE_NIGHT", "PREF_PREFER_IPV6", "PREF_PROXY_SHARING", "PREF_ALLOW_INSECURE", "PREF_SOCKS_PORT", "PREF_REMOTE_DNS", "PREF_DOMESTIC_DNS", "PREF_DNS_HOSTS", "PREF_DELAY_TEST_URL", "PREF_IP_API_URL", "PREF_LOGLEVEL", "PREF_OUTBOUND_DOMAIN_RESOLVE_METHOD", "PREF_MODE", "PREF_IS_BOOTED", "PREF_CHECK_UPDATE_PRE_RELEASE", "PREF_GEO_FILES_SOURCES", "PREF_USE_HEV_TUNNEL", "PREF_HEV_TUNNEL_LOGLEVEL", "PREF_HEV_TUNNEL_RW_TIMEOUT", "PREF_AUTO_REMOVE_INVALID_AFTER_TEST", "PREF_AUTO_SORT_AFTER_TEST", "CACHE_SUBSCRIPTION_ID", "PROTOCOL_FREEDOM", "BROADCAST_ACTION_SERVICE", "BROADCAST_ACTION_ACTIVITY", "BROADCAST_ACTION_WIDGET_CLICK", "TASKER_EXTRA_BUNDLE", "TASKER_EXTRA_STRING_BLURB", "TASKER_EXTRA_BUNDLE_SWITCH", "TASKER_EXTRA_BUNDLE_GUID", "TASKER_DEFAULT_GUID", "TAG_PROXY", "TAG_DIRECT", "TAG_BLOCKED", "TAG_FRAGMENT", "TAG_DNS", "TAG_DOMESTIC_DNS", "TAG_BALANCER", "UPLINK", "DOWNLINK", "GITHUB_URL", "GITHUB_RAW_URL", "GITHUB_DOWNLOAD_URL", "ANDROID_PACKAGE_NAME_LIST_URL", "APP_URL", "APP_API_URL", "APP_ISSUES_URL", "APP_WIKI_MODE", "APP_PRIVACY_POLICY", "APP_PROMOTION_URL", "TG_CHANNEL_URL", "DELAY_TEST_URL", "DELAY_TEST_URL2", "IP_API_URL", "DNS_PROXY", "DNS_DIRECT", "DNS_VPN", "GEOSITE_PRIVATE", "GEOSITE_CN", "GEOIP_PRIVATE", "GEOIP_CN", "GEOSITE_DAT", "GEOIP_DAT", "GEOIP_ONLY_CN_PRIVATE_DAT", "GEOIP_ONLY_CN_PRIVATE_URL", "PORT_LOCAL_DNS", "PORT_SOCKS", "WIREGUARD_LOCAL_ADDRESS_V4", "WIREGUARD_LOCAL_ADDRESS_V6", "WIREGUARD_LOCAL_MTU", "LOOPBACK", "MSG_REGISTER_CLIENT", "", "MSG_STATE_RUNNING", "MSG_STATE_NOT_RUNNING", "MSG_UNREGISTER_CLIENT", "MSG_STATE_START", "MSG_STATE_START_SUCCESS", "MSG_STATE_START_FAILURE", "MSG_STATE_STOP", "MSG_STATE_STOP_SUCCESS", "MSG_STATE_RESTART", "MSG_MEASURE_DELAY", "MSG_MEASURE_DELAY_SUCCESS", "MSG_MEASURE_CONFIG", "MSG_MEASURE_CONFIG_SUCCESS", "MSG_MEASURE_CONFIG_CANCEL", "MSG_MEASURE_CONFIG_NOTIFY", "MSG_MEASURE_CONFIG_FINISH", "RAY_NG_CHANNEL_ID", "RAY_NG_CHANNEL_NAME", "SUBSCRIPTION_UPDATE_CHANNEL", "SUBSCRIPTION_UPDATE_CHANNEL_NAME", "VMESS", "CUSTOM", "SHADOWSOCKS", "SOCKS", "HTTP", "VLESS", "TROJAN", "WIREGUARD", "TUIC", "HYSTERIA", "HYSTERIA2", "HY2", "VPN", "VPN_MTU", "HEVTUN_RW_TIMEOUT", "GOOGLEAPIS_CN_DOMAIN", "GOOGLEAPIS_COM_DOMAIN", "DNS_DNSPOD_DOMAIN", "DNS_ALIDNS_DOMAIN", "DNS_CLOUDFLARE_ONE_DOMAIN", "DNS_CLOUDFLARE_DNS_COM_DOMAIN", "DNS_CLOUDFLARE_DNS_DOMAIN", "DNS_GOOGLE_DOMAIN", "DNS_QUAD9_DOMAIN", "DNS_YANDEX_DOMAIN", "DEFAULT_PORT", "DEFAULT_SECURITY", "DEFAULT_LEVEL", "DEFAULT_NETWORK", "TLS", "REALITY", "HEADER_TYPE_HTTP", "DNS_ALIDNS_ADDRESSES", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "getDNS_ALIDNS_ADDRESSES", "()Ljava/util/ArrayList;", "DNS_CLOUDFLARE_ONE_ADDRESSES", "getDNS_CLOUDFLARE_ONE_ADDRESSES", "DNS_CLOUDFLARE_DNS_COM_ADDRESSES", "getDNS_CLOUDFLARE_DNS_COM_ADDRESSES", "DNS_CLOUDFLARE_DNS_ADDRESSES", "getDNS_CLOUDFLARE_DNS_ADDRESSES", "DNS_DNSPOD_ADDRESSES", "getDNS_DNSPOD_ADDRESSES", "DNS_GOOGLE_ADDRESSES", "getDNS_GOOGLE_ADDRESSES", "DNS_QUAD9_ADDRESSES", "getDNS_QUAD9_ADDRESSES", "DNS_YANDEX_ADDRESSES", "getDNS_YANDEX_ADDRESSES", "ROUTED_IP_LIST", "getROUTED_IP_LIST", "PRIVATE_IP_LIST", "getPRIVATE_IP_LIST", "GEO_FILES_SOURCES", "getGEO_FILES_SOURCES", "app_playstoreDebug"})
public final class AppConfig {
    
    /**
     * The application's package name.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ANG_PACKAGE = "com.v2ray.ang";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG = "com.v2ray.ang";
    
    /**
     * Directory names used in the app's file system.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DIR_ASSETS = "assets";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WEBDAV_BACKUP_DIR = "backups";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WEBDAV_BACKUP_FILE_NAME = "backup_ng.zip";
    
    /**
     * Legacy configuration keys.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ANG_CONFIG = "ang_config";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_SUBSCRIPTION_ID = "__default_subscription__";
    
    /**
     * Preferences mapped to MMKV storage.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_SNIFFING_ENABLED = "pref_sniffing_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_ROUTE_ONLY_ENABLED = "pref_route_only_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_PER_APP_PROXY = "pref_per_app_proxy";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_PER_APP_PROXY_SET = "pref_per_app_proxy_set";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_BYPASS_APPS = "pref_bypass_apps";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_LOCAL_DNS_ENABLED = "pref_local_dns_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_FAKE_DNS_ENABLED = "pref_fake_dns_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_APPEND_HTTP_PROXY = "pref_append_http_proxy";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_LOCAL_DNS_PORT = "pref_local_dns_port";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_VPN_DNS = "pref_vpn_dns";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_VPN_BYPASS_LAN = "pref_vpn_bypass_lan";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_VPN_INTERFACE_ADDRESS_CONFIG_INDEX = "pref_vpn_interface_address_config_index";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_VPN_MTU = "pref_vpn_mtu";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_ROUTING_DOMAIN_STRATEGY = "pref_routing_domain_strategy";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_ROUTING_RULESET = "pref_routing_ruleset";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_MUX_ENABLED = "pref_mux_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_MUX_CONCURRENCY = "pref_mux_concurrency";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_MUX_XUDP_CONCURRENCY = "pref_mux_xudp_concurrency";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_MUX_XUDP_QUIC = "pref_mux_xudp_quic";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_FRAGMENT_ENABLED = "pref_fragment_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_FRAGMENT_PACKETS = "pref_fragment_packets";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_FRAGMENT_LENGTH = "pref_fragment_length";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_FRAGMENT_INTERVAL = "pref_fragment_interval";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_AUTO_UPDATE = "pref_auto_update_subscription";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_AUTO_UPDATE_INTERVAL = "pref_auto_update_interval";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_DEFAULT_UPDATE_INTERVAL = "1440";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_UPDATE_TASK_NAME = "subscription_updater";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_SPEED_ENABLED = "pref_speed_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_CONFIRM_REMOVE = "pref_confirm_remove";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_START_SCAN_IMMEDIATE = "pref_start_scan_immediate";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_DOUBLE_COLUMN_DISPLAY = "pref_double_column_display";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_GROUP_ALL_DISPLAY = "pref_group_all_display";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_LANGUAGE = "pref_language";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_UI_MODE_NIGHT = "pref_ui_mode_night";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_PREFER_IPV6 = "pref_prefer_ipv6";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_PROXY_SHARING = "pref_proxy_sharing_enabled";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_ALLOW_INSECURE = "pref_allow_insecure";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_SOCKS_PORT = "pref_socks_port";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_REMOTE_DNS = "pref_remote_dns";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_DOMESTIC_DNS = "pref_domestic_dns";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_DNS_HOSTS = "pref_dns_hosts";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_DELAY_TEST_URL = "pref_delay_test_url";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_IP_API_URL = "pref_ip_api_url";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_LOGLEVEL = "pref_core_loglevel";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_OUTBOUND_DOMAIN_RESOLVE_METHOD = "pref_outbound_domain_resolve_method";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_MODE = "pref_mode";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_IS_BOOTED = "pref_is_booted";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_CHECK_UPDATE_PRE_RELEASE = "pref_check_update_pre_release";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_GEO_FILES_SOURCES = "pref_geo_files_sources";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_USE_HEV_TUNNEL = "pref_use_hev_tunnel_v2";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_HEV_TUNNEL_LOGLEVEL = "pref_hev_tunnel_loglevel";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_HEV_TUNNEL_RW_TIMEOUT = "pref_hev_tunnel_rw_timeout_v2";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_AUTO_REMOVE_INVALID_AFTER_TEST = "pref_auto_remove_invalid_after_test";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_AUTO_SORT_AFTER_TEST = "pref_auto_sort_after_test";
    
    /**
     * Cache keys.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CACHE_SUBSCRIPTION_ID = "cache_subscription_id";
    
    /**
     * Protocol identifiers.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PROTOCOL_FREEDOM = "freedom";
    
    /**
     * Broadcast actions.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BROADCAST_ACTION_SERVICE = "com.v2ray.ang.action.service";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BROADCAST_ACTION_ACTIVITY = "com.v2ray.ang.action.activity";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BROADCAST_ACTION_WIDGET_CLICK = "com.v2ray.ang.action.widget.click";
    
    /**
     * Tasker extras.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TASKER_EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TASKER_EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TASKER_EXTRA_BUNDLE_SWITCH = "tasker_extra_bundle_switch";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TASKER_EXTRA_BUNDLE_GUID = "tasker_extra_bundle_guid";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TASKER_DEFAULT_GUID = "Default";
    
    /**
     * Tags for different proxy modes.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_PROXY = "proxy";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_DIRECT = "direct";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_BLOCKED = "block";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_FRAGMENT = "fragment";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_DNS = "dns-module";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_DOMESTIC_DNS = "domestic-dns";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_BALANCER = "proxy-round";
    
    /**
     * Network-related constants.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String UPLINK = "uplink";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DOWNLINK = "downlink";
    
    /**
     * URLs for various resources.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GITHUB_URL = "https://github.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GITHUB_RAW_URL = "https://raw.githubusercontent.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GITHUB_DOWNLOAD_URL = "https://github.com/%s/releases/latest/download";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ANDROID_PACKAGE_NAME_LIST_URL = "https://raw.githubusercontent.com/2dust/androidpackagenamelist/master/proxy.txt";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_URL = "https://github.com/2dust/v2rayNG";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_API_URL = "https://api.github.com/repos/2dust/v2rayNG/releases";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_ISSUES_URL = "https://github.com/2dust/v2rayNG/issues";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_WIKI_MODE = "https://github.com/2dust/v2rayNG/wiki/Mode";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_PRIVACY_POLICY = "https://raw.githubusercontent.com/2dust/v2rayNG/master/CR.md";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_PROMOTION_URL = "aHR0cHM6Ly85LjIzNDQ1Ni54eXovYWJjLmh0bWw=";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TG_CHANNEL_URL = "https://t.me/github_2dust";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DELAY_TEST_URL = "https://www.gstatic.com/generate_204";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DELAY_TEST_URL2 = "https://www.google.com/generate_204";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String IP_API_URL = "https://api.ip.sb/geoip";
    
    /**
     * DNS server addresses.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_PROXY = "1.1.1.1";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_DIRECT = "223.5.5.5";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_VPN = "1.1.1.1";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOSITE_PRIVATE = "geosite:private";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOSITE_CN = "geosite:cn";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOIP_PRIVATE = "geoip:private";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOIP_CN = "geoip:cn";
    
    /**
     * Geo data file names.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOSITE_DAT = "geosite.dat";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOIP_DAT = "geoip.dat";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOIP_ONLY_CN_PRIVATE_DAT = "geoip-only-cn-private.dat";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GEOIP_ONLY_CN_PRIVATE_URL = "https://raw.githubusercontent.com/Loyalsoldier/geoip/release/geoip-only-cn-private.dat";
    
    /**
     * Ports and addresses for various services.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PORT_LOCAL_DNS = "10853";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PORT_SOCKS = "10808";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WIREGUARD_LOCAL_ADDRESS_V4 = "172.16.0.2/32";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WIREGUARD_LOCAL_ADDRESS_V6 = "2606:4700:110:8f81:d551:a0:532e:a2b3/128";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WIREGUARD_LOCAL_MTU = "1420";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOOPBACK = "127.0.0.1";
    
    /**
     * Message constants for communication.
     */
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_STATE_RUNNING = 11;
    public static final int MSG_STATE_NOT_RUNNING = 12;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_STATE_START = 3;
    public static final int MSG_STATE_START_SUCCESS = 31;
    public static final int MSG_STATE_START_FAILURE = 32;
    public static final int MSG_STATE_STOP = 4;
    public static final int MSG_STATE_STOP_SUCCESS = 41;
    public static final int MSG_STATE_RESTART = 5;
    public static final int MSG_MEASURE_DELAY = 6;
    public static final int MSG_MEASURE_DELAY_SUCCESS = 61;
    public static final int MSG_MEASURE_CONFIG = 7;
    public static final int MSG_MEASURE_CONFIG_SUCCESS = 71;
    public static final int MSG_MEASURE_CONFIG_CANCEL = 72;
    public static final int MSG_MEASURE_CONFIG_NOTIFY = 73;
    public static final int MSG_MEASURE_CONFIG_FINISH = 74;
    
    /**
     * Notification channel IDs and names.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String RAY_NG_CHANNEL_ID = "RAY_NG_M_CH_ID";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String RAY_NG_CHANNEL_NAME = "v2rayNG Background Service";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_UPDATE_CHANNEL = "subscription_update_channel";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUBSCRIPTION_UPDATE_CHANNEL_NAME = "Subscription Update Service";
    
    /**
     * Protocols Scheme
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VMESS = "vmess://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CUSTOM = "";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SHADOWSOCKS = "ss://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SOCKS = "socks://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HTTP = "http://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VLESS = "vless://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TROJAN = "trojan://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WIREGUARD = "wireguard://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TUIC = "tuic://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HYSTERIA = "hysteria://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HYSTERIA2 = "hysteria2://";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HY2 = "hy2://";
    
    /**
     * Give a good name to this, IDK
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VPN = "VPN";
    public static final int VPN_MTU = 1500;
    
    /**
     * hev-sock5-tunnel read-write-timeout value
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HEVTUN_RW_TIMEOUT = "300,60";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GOOGLEAPIS_CN_DOMAIN = "domain:googleapis.cn";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GOOGLEAPIS_COM_DOMAIN = "googleapis.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_DNSPOD_DOMAIN = "dot.pub";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_ALIDNS_DOMAIN = "dns.alidns.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_CLOUDFLARE_ONE_DOMAIN = "one.one.one.one";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_CLOUDFLARE_DNS_COM_DOMAIN = "dns.cloudflare.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_CLOUDFLARE_DNS_DOMAIN = "cloudflare-dns.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_GOOGLE_DOMAIN = "dns.google";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_QUAD9_DOMAIN = "dns.quad9.net";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DNS_YANDEX_DOMAIN = "common.dot.dns.yandex.net";
    public static final int DEFAULT_PORT = 443;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_SECURITY = "auto";
    public static final int DEFAULT_LEVEL = 8;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_NETWORK = "tcp";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TLS = "tls";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String REALITY = "reality";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HEADER_TYPE_HTTP = "http";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_ALIDNS_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_CLOUDFLARE_ONE_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_CLOUDFLARE_DNS_COM_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_CLOUDFLARE_DNS_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_DNSPOD_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_GOOGLE_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_QUAD9_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> DNS_YANDEX_ADDRESSES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> ROUTED_IP_LIST = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> PRIVATE_IP_LIST = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<java.lang.String> GEO_FILES_SOURCES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.AppConfig INSTANCE = null;
    
    private AppConfig() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_ALIDNS_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_CLOUDFLARE_ONE_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_CLOUDFLARE_DNS_COM_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_CLOUDFLARE_DNS_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_DNSPOD_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_GOOGLE_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_QUAD9_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getDNS_YANDEX_ADDRESSES() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getROUTED_IP_LIST() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getPRIVATE_IP_LIST() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getGEO_FILES_SOURCES() {
        return null;
    }
}