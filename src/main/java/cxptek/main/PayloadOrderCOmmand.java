package cxptek.main;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.InvalidMarshallableException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class PayloadOrderCOmmand implements BytesMarshallable {
    long id1;
    long id2;
    long id3;
    long id4;
    long id5;
    long id6;
    long id7;
    long id8;
    long id9;
    long id10;
    long id11;
    long id12;
    long id13;
    long id14;
    long id15;
    long id16;
//    long x[] = new long[48];

    public PayloadOrderCOmmand() {
        id1=System.nanoTime();
        id2=System.nanoTime();
        id3=System.nanoTime();
        id4=System.nanoTime();
        id5=System.nanoTime();
        id6=System.nanoTime();
        id7=System.nanoTime();
        id8=System.nanoTime();
        id9=System.nanoTime();
        id10=System.nanoTime();
        id11=System.nanoTime();
        id12=System.nanoTime();
        id13=System.nanoTime();
        id14=System.nanoTime();
        id15=System.nanoTime();
        id16=System.nanoTime();
//        for (int i=0;i<48;i++) {
//            x[i]=System.nanoTime();
//        }
    }

    @Override
    public void readMarshallable(BytesIn<?> bytes) throws IORuntimeException, BufferUnderflowException, IllegalStateException, InvalidMarshallableException {
        id1 = bytes.readLong();
        id2 = bytes.readLong();
        id3 = bytes.readLong();
        id4 = bytes.readLong();
        id5 = bytes.readLong();
        id6 = bytes.readLong();
        id7 = bytes.readLong();
        id8 = bytes.readLong();
        id9 = bytes.readLong();
        id10 = bytes.readLong();
        id11 = bytes.readLong();
        id12 = bytes.readLong();
        id13 = bytes.readLong();
        id14 = bytes.readLong();
        id15 = bytes.readLong();
        id16 = bytes.readLong();
//        for (int i=0;i<48;i++) {
//            x[i]=bytes.readLong();
//        }
    }

    @Override
    public void writeMarshallable(BytesOut<?> bytes) throws IllegalStateException, BufferOverflowException, BufferUnderflowException, ArithmeticException, InvalidMarshallableException {
        bytes.writeLong(id1);
        bytes.writeLong(id2);
        bytes.writeLong(id3);
        bytes.writeLong(id4);
        bytes.writeLong(id5);
        bytes.writeLong(id6);
        bytes.writeLong(id7);
        bytes.writeLong(id8);
        bytes.writeLong(id9);
        bytes.writeLong(id10);
        bytes.writeLong(id11);
        bytes.writeLong(id12);
        bytes.writeLong(id13);
        bytes.writeLong(id14);
        bytes.writeLong(id15);
        bytes.writeLong(id16);
//        for (int i=0;i<48;i++) {
//            bytes.writeLong(x[i]);
//        }
    }
}