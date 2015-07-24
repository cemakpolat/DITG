CFLAGS = $(subst -O2,-O0,$(CXXFLAGS))

all:   ITGDec 

ITGDec:  ITGDecod.cpp
	$(CXX) $(CFLAGS) ITGDecod.cpp -lm -o $@$(SUFFIX) $(LDFLAGS)
	$(MV) $@$(SUFFIX) "$(BIN)"

#ITGDec-23:  ITGDecod.cpp
#	$(CXX) $(CXXFLAGS) ITGDecod.cpp -lm -DV23 -o $@$(SUFFIX) $(LDFLAGS)
#	$(CP) $@$(SUFFIX) "$(BIN)"

clean:
	$(RM) *.o ITGDec$(SUFFIX) "$(BIN)"ITGDec*
#	$(RM) *.o ITGDec-23$(SUFFIX) "$(BIN)"ITGDec-23$(SUFFIX)

