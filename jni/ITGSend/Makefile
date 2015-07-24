NRDIR = newran
OBJS = ../common/serial.o ../common/ITG.o ../common/timestamp.o ../$(THOBJS) traffic.o ../common/pipes.o

all:   ITGSend

ITGSend: ../common/ITG.h ITGSend.cpp $(NRDIR)/libnewran.a $(OBJS)
	$(CXX) $(CXXFLAGS) -I$(NRDIR) -L$(NRDIR) $(OBJS) ITGSend.cpp -lnewran -o $@$(SUFFIX) $(LDFLAGS)
	$(MV) $@$(SUFFIX) "$(BIN)"

$(NRDIR)/libnewran.a: newran
	$(MAKE) -C $(NRDIR) -ef nr_gnu.mak $(@F)

newran:
#	mkdir newran; cd newran; tar xzf ../newran03.tar.gz; cd ..

clean:
	$(RM) *.o ITGSend$(SUFFIX) "$(BIN)"ITGSend$(SUFFIX)
	$(RM) $(NRDIR)/*.o $(NRDIR)/*.a

