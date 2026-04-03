package com.v2ray.ang.dto;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00005\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0003\b\u0086\u0001\n\u0002\u0010!\n\u0002\b;\b\u0086\b\u0018\u0000 \u00e4\u00012\u00020\u0001:\u0002\u00e4\u0001B\u00d3\u0004\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010 \u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#\u0012\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\'\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010*\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010+\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010,\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010-\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010.\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u00100\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00101\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00102\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00103\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00104\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00105\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00106\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00107\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u00108\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0004\b9\u0010:J\u000e\u0010\u00a9\u0001\u001a\t\u0012\u0004\u0012\u00020\u00070\u00aa\u0001J\u0007\u0010\u00ab\u0001\u001a\u00020\u0007J\u0015\u0010\u00ac\u0001\u001a\u00020#2\t\u0010\u00ad\u0001\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\n\u0010\u00ae\u0001\u001a\u00020\u0003H\u00c6\u0003J\n\u0010\u00af\u0001\u001a\u00020\u0005H\u00c6\u0003J\n\u0010\u00b0\u0001\u001a\u00020\u0007H\u00c6\u0003J\n\u0010\u00b1\u0001\u001a\u00020\tH\u00c6\u0003J\n\u0010\u00b2\u0001\u001a\u00020\u0007H\u00c6\u0003J\f\u0010\u00b3\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b4\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b5\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b6\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b7\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b8\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00b9\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00ba\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00bb\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00bc\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00bd\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00be\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00bf\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c0\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c1\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c2\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c3\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c4\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c5\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c6\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c7\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c8\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00c9\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0011\u0010\u00ca\u0001\u001a\u0004\u0018\u00010#H\u00c6\u0003\u00a2\u0006\u0002\u0010xJ\f\u0010\u00cb\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00cc\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00cd\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00ce\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00cf\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d0\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d1\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d2\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d3\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d4\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d5\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u0012\u0010\u00d6\u0001\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0003\u0010\u0093\u0001J\f\u0010\u00d7\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d8\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00d9\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00da\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00db\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00dc\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00dd\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00de\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\f\u0010\u00df\u0001\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u00de\u0004\u0010\u00e0\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010 \u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010&\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\'\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010*\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010+\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010,\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010-\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010.\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u00100\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00101\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00102\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00103\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00104\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00105\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00106\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00107\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u00108\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001\u00a2\u0006\u0003\u0010\u00e1\u0001J\n\u0010\u00e2\u0001\u001a\u00020\u0003H\u00d6\u0001J\n\u0010\u00e3\u0001\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b?\u0010@\"\u0004\bA\u0010BR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bC\u0010D\"\u0004\bE\u0010FR\u001a\u0010\n\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bG\u0010@\"\u0004\bH\u0010BR\u001c\u0010\u000b\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bI\u0010@\"\u0004\bJ\u0010BR\u001c\u0010\f\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bK\u0010@\"\u0004\bL\u0010BR\u001c\u0010\r\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bM\u0010@\"\u0004\bN\u0010BR\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bO\u0010@\"\u0004\bP\u0010BR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bQ\u0010@\"\u0004\bR\u0010BR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bS\u0010@\"\u0004\bT\u0010BR\u001c\u0010\u0011\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bU\u0010@\"\u0004\bV\u0010BR\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bW\u0010@\"\u0004\bX\u0010BR\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bY\u0010@\"\u0004\bZ\u0010BR\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b[\u0010@\"\u0004\b\\\u0010BR\u001c\u0010\u0015\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b]\u0010@\"\u0004\b^\u0010BR\u001c\u0010\u0016\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b_\u0010@\"\u0004\b`\u0010BR\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\ba\u0010@\"\u0004\bb\u0010BR\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bc\u0010@\"\u0004\bd\u0010BR\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\be\u0010@\"\u0004\bf\u0010BR\u001c\u0010\u001a\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bg\u0010@\"\u0004\bh\u0010BR\u001c\u0010\u001b\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bi\u0010@\"\u0004\bj\u0010BR\u001c\u0010\u001c\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bk\u0010@\"\u0004\bl\u0010BR\u001c\u0010\u001d\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bm\u0010@\"\u0004\bn\u0010BR\u001c\u0010\u001e\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bo\u0010@\"\u0004\bp\u0010BR\u001c\u0010\u001f\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bq\u0010@\"\u0004\br\u0010BR\u001c\u0010 \u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bs\u0010@\"\u0004\bt\u0010BR\u001c\u0010!\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bu\u0010@\"\u0004\bv\u0010BR\u001e\u0010\"\u001a\u0004\u0018\u00010#X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010{\u001a\u0004\bw\u0010x\"\u0004\by\u0010zR\u001c\u0010$\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b|\u0010@\"\u0004\b}\u0010BR\u001c\u0010%\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b~\u0010@\"\u0004\b\u007f\u0010BR\u001e\u0010&\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0080\u0001\u0010@\"\u0005\b\u0081\u0001\u0010BR\u001e\u0010\'\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0082\u0001\u0010@\"\u0005\b\u0083\u0001\u0010BR\u001e\u0010(\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0084\u0001\u0010@\"\u0005\b\u0085\u0001\u0010BR\u001e\u0010)\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0086\u0001\u0010@\"\u0005\b\u0087\u0001\u0010BR\u001e\u0010*\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0088\u0001\u0010@\"\u0005\b\u0089\u0001\u0010BR\u001e\u0010+\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u008a\u0001\u0010@\"\u0005\b\u008b\u0001\u0010BR\u001e\u0010,\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u008c\u0001\u0010@\"\u0005\b\u008d\u0001\u0010BR\u001e\u0010-\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u008e\u0001\u0010@\"\u0005\b\u008f\u0001\u0010BR\u001e\u0010.\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0090\u0001\u0010@\"\u0005\b\u0091\u0001\u0010BR#\u0010/\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u0015\n\u0003\u0010\u0096\u0001\u001a\u0006\b\u0092\u0001\u0010\u0093\u0001\"\u0006\b\u0094\u0001\u0010\u0095\u0001R\u001e\u00100\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0097\u0001\u0010@\"\u0005\b\u0098\u0001\u0010BR\u001e\u00101\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0099\u0001\u0010@\"\u0005\b\u009a\u0001\u0010BR\u001e\u00102\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009b\u0001\u0010@\"\u0005\b\u009c\u0001\u0010BR\u001e\u00103\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009d\u0001\u0010@\"\u0005\b\u009e\u0001\u0010BR\u001e\u00104\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009f\u0001\u0010@\"\u0005\b\u00a0\u0001\u0010BR\u001e\u00105\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a1\u0001\u0010@\"\u0005\b\u00a2\u0001\u0010BR\u001e\u00106\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a3\u0001\u0010@\"\u0005\b\u00a4\u0001\u0010BR\u001e\u00107\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a5\u0001\u0010@\"\u0005\b\u00a6\u0001\u0010BR\u001e\u00108\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a7\u0001\u0010@\"\u0005\b\u00a8\u0001\u0010B\u00a8\u0006\u00e5\u0001"}, d2 = {"Lcom/v2ray/ang/dto/ProfileItem;", "", "configVersion", "", "configType", "Lcom/v2ray/ang/enums/EConfigType;", "subscriptionId", "", "addedTime", "", "remarks", "description", "server", "serverPort", "password", "method", "flow", "username", "network", "headerType", "host", "path", "seed", "quicSecurity", "quicKey", "mode", "serviceName", "authority", "xhttpMode", "xhttpExtra", "security", "sni", "alpn", "fingerPrint", "insecure", "", "echConfigList", "echForceQuery", "pinnedCA256", "publicKey", "shortId", "spiderX", "mldsa65Verify", "secretKey", "preSharedKey", "localAddress", "reserved", "mtu", "obfsPassword", "portHopping", "portHoppingInterval", "pinSHA256", "bandwidthDown", "bandwidthUp", "policyGroupType", "policyGroupSubscriptionId", "policyGroupFilter", "<init>", "(ILcom/v2ray/ang/enums/EConfigType;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getConfigVersion", "()I", "getConfigType", "()Lcom/v2ray/ang/enums/EConfigType;", "getSubscriptionId", "()Ljava/lang/String;", "setSubscriptionId", "(Ljava/lang/String;)V", "getAddedTime", "()J", "setAddedTime", "(J)V", "getRemarks", "setRemarks", "getDescription", "setDescription", "getServer", "setServer", "getServerPort", "setServerPort", "getPassword", "setPassword", "getMethod", "setMethod", "getFlow", "setFlow", "getUsername", "setUsername", "getNetwork", "setNetwork", "getHeaderType", "setHeaderType", "getHost", "setHost", "getPath", "setPath", "getSeed", "setSeed", "getQuicSecurity", "setQuicSecurity", "getQuicKey", "setQuicKey", "getMode", "setMode", "getServiceName", "setServiceName", "getAuthority", "setAuthority", "getXhttpMode", "setXhttpMode", "getXhttpExtra", "setXhttpExtra", "getSecurity", "setSecurity", "getSni", "setSni", "getAlpn", "setAlpn", "getFingerPrint", "setFingerPrint", "getInsecure", "()Ljava/lang/Boolean;", "setInsecure", "(Ljava/lang/Boolean;)V", "Ljava/lang/Boolean;", "getEchConfigList", "setEchConfigList", "getEchForceQuery", "setEchForceQuery", "getPinnedCA256", "setPinnedCA256", "getPublicKey", "setPublicKey", "getShortId", "setShortId", "getSpiderX", "setSpiderX", "getMldsa65Verify", "setMldsa65Verify", "getSecretKey", "setSecretKey", "getPreSharedKey", "setPreSharedKey", "getLocalAddress", "setLocalAddress", "getReserved", "setReserved", "getMtu", "()Ljava/lang/Integer;", "setMtu", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "getObfsPassword", "setObfsPassword", "getPortHopping", "setPortHopping", "getPortHoppingInterval", "setPortHoppingInterval", "getPinSHA256", "setPinSHA256", "getBandwidthDown", "setBandwidthDown", "getBandwidthUp", "setBandwidthUp", "getPolicyGroupType", "setPolicyGroupType", "getPolicyGroupSubscriptionId", "setPolicyGroupSubscriptionId", "getPolicyGroupFilter", "setPolicyGroupFilter", "getAllOutboundTags", "", "getServerAddressAndPort", "equals", "other", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component30", "component31", "component32", "component33", "component34", "component35", "component36", "component37", "component38", "component39", "component40", "component41", "component42", "component43", "component44", "component45", "component46", "component47", "component48", "component49", "component50", "copy", "(ILcom/v2ray/ang/enums/EConfigType;Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/v2ray/ang/dto/ProfileItem;", "hashCode", "toString", "Companion", "app_playstoreDebug"})
public final class ProfileItem {
    private final int configVersion = 0;
    @org.jetbrains.annotations.NotNull()
    private final com.v2ray.ang.enums.EConfigType configType = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String subscriptionId;
    private long addedTime;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String remarks;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String description;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String server;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String serverPort;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String password;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String method;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String flow;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String username;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String network;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String headerType;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String host;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String path;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String seed;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String quicSecurity;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String quicKey;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String mode;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String serviceName;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String authority;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String xhttpMode;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String xhttpExtra;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String security;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String sni;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String alpn;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String fingerPrint;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Boolean insecure;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String echConfigList;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String echForceQuery;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pinnedCA256;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String publicKey;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String shortId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String spiderX;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String mldsa65Verify;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String secretKey;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String preSharedKey;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String localAddress;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String reserved;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer mtu;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String obfsPassword;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String portHopping;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String portHoppingInterval;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String pinSHA256;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String bandwidthDown;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String bandwidthUp;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String policyGroupType;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String policyGroupSubscriptionId;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String policyGroupFilter;
    @org.jetbrains.annotations.NotNull()
    public static final com.v2ray.ang.dto.ProfileItem.Companion Companion = null;
    
    public ProfileItem(int configVersion, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.enums.EConfigType configType, @org.jetbrains.annotations.NotNull()
    java.lang.String subscriptionId, long addedTime, @org.jetbrains.annotations.NotNull()
    java.lang.String remarks, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.String server, @org.jetbrains.annotations.Nullable()
    java.lang.String serverPort, @org.jetbrains.annotations.Nullable()
    java.lang.String password, @org.jetbrains.annotations.Nullable()
    java.lang.String method, @org.jetbrains.annotations.Nullable()
    java.lang.String flow, @org.jetbrains.annotations.Nullable()
    java.lang.String username, @org.jetbrains.annotations.Nullable()
    java.lang.String network, @org.jetbrains.annotations.Nullable()
    java.lang.String headerType, @org.jetbrains.annotations.Nullable()
    java.lang.String host, @org.jetbrains.annotations.Nullable()
    java.lang.String path, @org.jetbrains.annotations.Nullable()
    java.lang.String seed, @org.jetbrains.annotations.Nullable()
    java.lang.String quicSecurity, @org.jetbrains.annotations.Nullable()
    java.lang.String quicKey, @org.jetbrains.annotations.Nullable()
    java.lang.String mode, @org.jetbrains.annotations.Nullable()
    java.lang.String serviceName, @org.jetbrains.annotations.Nullable()
    java.lang.String authority, @org.jetbrains.annotations.Nullable()
    java.lang.String xhttpMode, @org.jetbrains.annotations.Nullable()
    java.lang.String xhttpExtra, @org.jetbrains.annotations.Nullable()
    java.lang.String security, @org.jetbrains.annotations.Nullable()
    java.lang.String sni, @org.jetbrains.annotations.Nullable()
    java.lang.String alpn, @org.jetbrains.annotations.Nullable()
    java.lang.String fingerPrint, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean insecure, @org.jetbrains.annotations.Nullable()
    java.lang.String echConfigList, @org.jetbrains.annotations.Nullable()
    java.lang.String echForceQuery, @org.jetbrains.annotations.Nullable()
    java.lang.String pinnedCA256, @org.jetbrains.annotations.Nullable()
    java.lang.String publicKey, @org.jetbrains.annotations.Nullable()
    java.lang.String shortId, @org.jetbrains.annotations.Nullable()
    java.lang.String spiderX, @org.jetbrains.annotations.Nullable()
    java.lang.String mldsa65Verify, @org.jetbrains.annotations.Nullable()
    java.lang.String secretKey, @org.jetbrains.annotations.Nullable()
    java.lang.String preSharedKey, @org.jetbrains.annotations.Nullable()
    java.lang.String localAddress, @org.jetbrains.annotations.Nullable()
    java.lang.String reserved, @org.jetbrains.annotations.Nullable()
    java.lang.Integer mtu, @org.jetbrains.annotations.Nullable()
    java.lang.String obfsPassword, @org.jetbrains.annotations.Nullable()
    java.lang.String portHopping, @org.jetbrains.annotations.Nullable()
    java.lang.String portHoppingInterval, @org.jetbrains.annotations.Nullable()
    java.lang.String pinSHA256, @org.jetbrains.annotations.Nullable()
    java.lang.String bandwidthDown, @org.jetbrains.annotations.Nullable()
    java.lang.String bandwidthUp, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupType, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupSubscriptionId, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupFilter) {
        super();
    }
    
    public final int getConfigVersion() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.enums.EConfigType getConfigType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSubscriptionId() {
        return null;
    }
    
    public final void setSubscriptionId(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public final long getAddedTime() {
        return 0L;
    }
    
    public final void setAddedTime(long p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRemarks() {
        return null;
    }
    
    public final void setRemarks(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    public final void setDescription(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getServer() {
        return null;
    }
    
    public final void setServer(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getServerPort() {
        return null;
    }
    
    public final void setServerPort(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPassword() {
        return null;
    }
    
    public final void setPassword(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMethod() {
        return null;
    }
    
    public final void setMethod(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFlow() {
        return null;
    }
    
    public final void setFlow(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getUsername() {
        return null;
    }
    
    public final void setUsername(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNetwork() {
        return null;
    }
    
    public final void setNetwork(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getHeaderType() {
        return null;
    }
    
    public final void setHeaderType(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getHost() {
        return null;
    }
    
    public final void setHost(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPath() {
        return null;
    }
    
    public final void setPath(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSeed() {
        return null;
    }
    
    public final void setSeed(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getQuicSecurity() {
        return null;
    }
    
    public final void setQuicSecurity(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getQuicKey() {
        return null;
    }
    
    public final void setQuicKey(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMode() {
        return null;
    }
    
    public final void setMode(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getServiceName() {
        return null;
    }
    
    public final void setServiceName(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAuthority() {
        return null;
    }
    
    public final void setAuthority(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getXhttpMode() {
        return null;
    }
    
    public final void setXhttpMode(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getXhttpExtra() {
        return null;
    }
    
    public final void setXhttpExtra(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSecurity() {
        return null;
    }
    
    public final void setSecurity(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSni() {
        return null;
    }
    
    public final void setSni(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAlpn() {
        return null;
    }
    
    public final void setAlpn(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFingerPrint() {
        return null;
    }
    
    public final void setFingerPrint(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getInsecure() {
        return null;
    }
    
    public final void setInsecure(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getEchConfigList() {
        return null;
    }
    
    public final void setEchConfigList(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getEchForceQuery() {
        return null;
    }
    
    public final void setEchForceQuery(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPinnedCA256() {
        return null;
    }
    
    public final void setPinnedCA256(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPublicKey() {
        return null;
    }
    
    public final void setPublicKey(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getShortId() {
        return null;
    }
    
    public final void setShortId(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSpiderX() {
        return null;
    }
    
    public final void setSpiderX(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMldsa65Verify() {
        return null;
    }
    
    public final void setMldsa65Verify(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSecretKey() {
        return null;
    }
    
    public final void setSecretKey(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPreSharedKey() {
        return null;
    }
    
    public final void setPreSharedKey(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLocalAddress() {
        return null;
    }
    
    public final void setLocalAddress(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getReserved() {
        return null;
    }
    
    public final void setReserved(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getMtu() {
        return null;
    }
    
    public final void setMtu(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getObfsPassword() {
        return null;
    }
    
    public final void setObfsPassword(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPortHopping() {
        return null;
    }
    
    public final void setPortHopping(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPortHoppingInterval() {
        return null;
    }
    
    public final void setPortHoppingInterval(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPinSHA256() {
        return null;
    }
    
    public final void setPinSHA256(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBandwidthDown() {
        return null;
    }
    
    public final void setBandwidthDown(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBandwidthUp() {
        return null;
    }
    
    public final void setBandwidthUp(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPolicyGroupType() {
        return null;
    }
    
    public final void setPolicyGroupType(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPolicyGroupSubscriptionId() {
        return null;
    }
    
    public final void setPolicyGroupSubscriptionId(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPolicyGroupFilter() {
        return null;
    }
    
    public final void setPolicyGroupFilter(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAllOutboundTags() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getServerAddressAndPort() {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.enums.EConfigType component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component21() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component22() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component23() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component24() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component26() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component27() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component28() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean component29() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component30() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component31() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component32() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component33() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component34() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component35() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component36() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component37() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component38() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component39() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component40() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component41() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component42() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component43() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component44() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component45() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component46() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component47() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component48() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component49() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component50() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.v2ray.ang.dto.ProfileItem copy(int configVersion, @org.jetbrains.annotations.NotNull()
    com.v2ray.ang.enums.EConfigType configType, @org.jetbrains.annotations.NotNull()
    java.lang.String subscriptionId, long addedTime, @org.jetbrains.annotations.NotNull()
    java.lang.String remarks, @org.jetbrains.annotations.Nullable()
    java.lang.String description, @org.jetbrains.annotations.Nullable()
    java.lang.String server, @org.jetbrains.annotations.Nullable()
    java.lang.String serverPort, @org.jetbrains.annotations.Nullable()
    java.lang.String password, @org.jetbrains.annotations.Nullable()
    java.lang.String method, @org.jetbrains.annotations.Nullable()
    java.lang.String flow, @org.jetbrains.annotations.Nullable()
    java.lang.String username, @org.jetbrains.annotations.Nullable()
    java.lang.String network, @org.jetbrains.annotations.Nullable()
    java.lang.String headerType, @org.jetbrains.annotations.Nullable()
    java.lang.String host, @org.jetbrains.annotations.Nullable()
    java.lang.String path, @org.jetbrains.annotations.Nullable()
    java.lang.String seed, @org.jetbrains.annotations.Nullable()
    java.lang.String quicSecurity, @org.jetbrains.annotations.Nullable()
    java.lang.String quicKey, @org.jetbrains.annotations.Nullable()
    java.lang.String mode, @org.jetbrains.annotations.Nullable()
    java.lang.String serviceName, @org.jetbrains.annotations.Nullable()
    java.lang.String authority, @org.jetbrains.annotations.Nullable()
    java.lang.String xhttpMode, @org.jetbrains.annotations.Nullable()
    java.lang.String xhttpExtra, @org.jetbrains.annotations.Nullable()
    java.lang.String security, @org.jetbrains.annotations.Nullable()
    java.lang.String sni, @org.jetbrains.annotations.Nullable()
    java.lang.String alpn, @org.jetbrains.annotations.Nullable()
    java.lang.String fingerPrint, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean insecure, @org.jetbrains.annotations.Nullable()
    java.lang.String echConfigList, @org.jetbrains.annotations.Nullable()
    java.lang.String echForceQuery, @org.jetbrains.annotations.Nullable()
    java.lang.String pinnedCA256, @org.jetbrains.annotations.Nullable()
    java.lang.String publicKey, @org.jetbrains.annotations.Nullable()
    java.lang.String shortId, @org.jetbrains.annotations.Nullable()
    java.lang.String spiderX, @org.jetbrains.annotations.Nullable()
    java.lang.String mldsa65Verify, @org.jetbrains.annotations.Nullable()
    java.lang.String secretKey, @org.jetbrains.annotations.Nullable()
    java.lang.String preSharedKey, @org.jetbrains.annotations.Nullable()
    java.lang.String localAddress, @org.jetbrains.annotations.Nullable()
    java.lang.String reserved, @org.jetbrains.annotations.Nullable()
    java.lang.Integer mtu, @org.jetbrains.annotations.Nullable()
    java.lang.String obfsPassword, @org.jetbrains.annotations.Nullable()
    java.lang.String portHopping, @org.jetbrains.annotations.Nullable()
    java.lang.String portHoppingInterval, @org.jetbrains.annotations.Nullable()
    java.lang.String pinSHA256, @org.jetbrains.annotations.Nullable()
    java.lang.String bandwidthDown, @org.jetbrains.annotations.Nullable()
    java.lang.String bandwidthUp, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupType, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupSubscriptionId, @org.jetbrains.annotations.Nullable()
    java.lang.String policyGroupFilter) {
        return null;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/v2ray/ang/dto/ProfileItem$Companion;", "", "<init>", "()V", "create", "Lcom/v2ray/ang/dto/ProfileItem;", "configType", "Lcom/v2ray/ang/enums/EConfigType;", "app_playstoreDebug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.v2ray.ang.dto.ProfileItem create(@org.jetbrains.annotations.NotNull()
        com.v2ray.ang.enums.EConfigType configType) {
            return null;
        }
    }
}