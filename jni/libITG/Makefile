ifdef LINUX_OS
	PIC := -fPIC
endif

all:   libITG.so

libITG.so: ITGapi.h ITGapi.cpp
	$(CXX) $(CXXFLAGS) $(PIC) -shared -o $@ ITGapi.cpp ../$(THOBJS) $(LDFLAGS)
	$(MV) $@ "$(BIN)"

clean:
	$(RM) *.o libITG.so "$(BIN)"libITG.so

