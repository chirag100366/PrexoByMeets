package main

import (
	"syscall"
	"fmt"
	"github.com/shirou/gopsutil/cpu"
	"github.com/shirou/gopsutil/mem"

	"strconv"
	"bytes"
	"encoding/json"
	"net/http"
)
func main(){
	var TWO_TO_TEN uint64;
	TWO_TO_TEN=1024;
	var stat syscall.Statfs_t
	syscall.Statfs("/", &stat)
	//fmt.Println((stat.Blocks * uint64(stat.Bsize))/(TWO_TO_TEN*TWO_TO_TEN*TWO_TO_TEN))
	//fmt.Println(runtime.NumCPU())
	a,_:=cpu.Info()
	//fmt.Println(a[0])
	//b,_:=host.Info()
	//fmt.Println(b)
	c,_:=mem.VirtualMemory()
	//fmt.Println(c.Total/(TWO_TO_TEN*TWO_TO_TEN))
	mp:=map[string]string{
		"diskSpace": strconv.Itoa(int((stat.Blocks * uint64(stat.Bsize))/(TWO_TO_TEN*TWO_TO_TEN*TWO_TO_TEN))),
		"cores":strconv.Itoa(int(a[0].Cores)),
		"cpuModel": a[0].ModelName,
		"ram":strconv.Itoa(int(c.Total/(TWO_TO_TEN*TWO_TO_TEN))),

				}
	buf:= new(bytes.Buffer)
	json.NewEncoder(buf).Encode(mp)
	url:="kachra"
	http.Post(url,"application/x-www-form-urlencoded",buf);
	fmt.Println(buf)



}

