all: ITGManager

OBJS = ../common/thread.o

ITGManager: ../libITG/ITGapi.h ../libITG/ITGapi.cpp ITGManager.cpp
	$(CXX) $(CXXFLAGS) $(OBJS) ITGManager.cpp ../libITG/ITGapi.cpp -o $@$(SUFFIX) $(LDFLAGS)
	$(MV) $@$(SUFFIX) "$(BIN)"

clean:
	$(RM) *.o ITGManager$(SUFFIX) "$(BIN)"ITGManager$(SUFFIX)

