# Lab3 实验报告

## 练习 1：理解基于 FIFO 的页面替换算法（思考题）

> 描述 FIFO 页面置换算法下，一个页面从被换入到被换出的过程中，会经过代码里哪些函数/宏的处理（或者说，需要调用哪些函数/宏），并用简单的一两句话描述每个函数在过程中做了什么？（为了方便同学们完成练习，所以实际上我们的项目代码和实验指导的还是略有不同，例如我们将 FIFO 页面置换算法头文件的大部分代码放在了 `kern/mm/swap_fifo.c` 文件中，这点请同学们注意）
>
> - 至少正确指出 10 个不同的函数分别做了什么？如果少于 10 个将酌情给分。我们认为只要函数原型不同，就算两个不同的函数。要求指出对执行过程有实际影响, 删去后会导致输出结果不同的函数（例如 assert）而不是 cprintf 这样的函数。如果你选择的函数不能完整地体现”从换入到换出“的过程，比如 10 个函数都是页面换入的时候调用的，或者解释功能的时候只解释了这 10 个函数在页面换入时的功能，那么也会扣除一定的分数

### 页面换入换出流程

当程序访问虚拟地址发生缺页异常时，会调用 `do_pgfault` 函数进行处理。在这个过程中涉及到页面的换入换出操作。`do_pgfault` 函数调用 `get_pte` 函数尝试获取与虚拟地址对应的页表项指针 `ptep`。

如果获取到的页表项 `*ptep` 的值为 0，表示该页表项不存在，此时需要调用 `pgdir_alloc_page` 函数为该虚拟地址在页表中分配一个新的页表项。如果分配失败，则输出错误信息并跳转到 `failed` 标签处，表示处理页面错误失败。

如果 `*ptep` 不等于 0，此时认为该页表项可能是一个交换项，即对应的页面数据可能存储在磁盘的交换空间中，需要进行从磁盘换入页面的操作。前提是 `swap_init_ok` 为真，即交换初始化成功，具体操作如下：

1. 调用 `swap_in` 函数，根据给定的内存管理结构体 `mm` 和虚拟地址 `addr`，从磁盘交换空间中将对应的页面数据换入到内存页面 `page` 中。在调用 `alloc_page` 分配内存空间的过程中，如果内存不足，则需要调用 `swap_out` 函数，使用页面替换算法来换出内存页。

2. 调用 `page_insert` 函数，将换入的物理页面 `page` 与虚拟地址 `addr` 建立映射关系，通过更新页表来插入新的页表项，确保虚拟地址到物理地址的映射正确。

3. 调用 `swap_map_swappable` 函数，将物理页面 `page` 标记为可交换状态，在后续的内存管理操作中，当需要进行页面换出等操作时，可以根据页面的可交换属性来选择合适的页面进行换出。

### 换入换出过程用到的函数

1. `do_pgfault()`

   当系统发生缺页异常后，程序会将跳转到该函数进行缺页处理。在该函数中会首先判断出错的虚拟地址在 `mm_struct` 里是否可用。如果可用：若查找的 `pte` 当前为空（表示该虚拟页没有映射），则调用 `pgdir_alloc_page` 分配物理页并建立页表映射。如果页表项不为空（`*ptep != 0`），表示需要进行页面换入操作，使用 `swap_in()` 函数换入页。

2. `swap_in()`

   用来将已经映射过并且当前在磁盘上的页换进内存中。

3. `alloc_pages()`

   分配指定数量的页。

4. `swap_out()`

   当使用 `alloc_page` 函数已经分配不到内存页的情况下，使用该函数把内存中的页从内存中替换出去。

5. `_fifo_swap_out_victim()`

   `FIFO` 替换方法的核心算法，用来将保存页面队列中最先进来的的内存页替换出去。

6. `get_pte()`

   从页表中找到指定地址的页表项。

7. `swapfs_write()`

   用于将页面写入磁盘。在这里由于需要换出页面，而页面内容如果被修改过那么就与磁盘中的不一致，所以需要将其重新写回磁盘。

8. `free_page()`

   将要替换的内存页释放。   

9. `swapfs_read()`

   用于将磁盘中的数据写入内存。 

10. `ide_read_secs()/ide_write_secs()`

    读/写指定数量的扇区数据     

11. `page_insert()`

    在页表中新增一个映射关系。     

12. `page_remove_pte()`

    从页表中删除指定地址的页表项。

13. `pte_create()`

    更新了页表项，使其指向要插入的页面 page。

14. `tlb_invalidate()`

    在替换内存页或更新页表映射之后用来将 `TLB` 刷新。

15. `swap_map_swappable()`

    用于将内存页放进交换区里面。

16. `_fifo_map_swappable()`

    将新进入内存的可交换页面链接到链表的末尾，以记录页面进入内存的顺序。

17. `swap_init()`

    用于初始化换页策略。

18. `ide_init()`

    用于初始化虚拟硬盘。

19. `check_swap()`

    用来测试换页策略。

20. `page_remove()`

    从页表中删除一个映射。

特别地，`swap_in()` 函数仅在 `do_pgfault()` 中被调用以处理缺页异常，而 TLB 的刷新操作则在后续的 `page_insert()` 中完成。相对地，`swap_out()` 函数可能在多种情况下被调用，如页面换入时、空闲页分配时等，只要符合消极策略的时机，该函数就会被触发。

## 练习 2：深入理解不同分页模式的工作原理（思考题）

> get_pte()函数（位于 `kern/mm/pmm.c`）用于在页表中查找或创建页表项，从而实现对指定线性地址对应的物理页的访问和映射操作。这在操作系统中的分页机制下，是实现虚拟内存与物理内存之间映射关系非常重要的内容。
>
> - get_pte()函数中有两段形式类似的代码， 结合 sv32，sv39，sv48 的异同，解释这两段代码为什么如此相像。
> - 目前 get_pte()函数将页表项的查找和页表项的分配合并在一个函数里，你认为这种写法好吗？有没有必要把两个功能拆开？

### sv32，sv39，sv48 的异同

1. **页表层级**

- **sv32**: 使用两级页表结构，适用于 32 位虚拟地址空间。地址由两部分组成：页目录项（10 位）和页表项（10 位），加上 12 位偏移。
- **sv39**: 使用三级页表结构，适用于 39 位虚拟地址空间。地址分为 3 个页目录项（9 位、9 位和 9 位），加上 12 位偏移。
- **sv48**: 使用四级页表结构，适用于 48 位虚拟地址空间。地址分为 4 个页目录项（9 位、9 位、9 位和 9 位），加上 12 位偏移。

2. **虚拟地址空间大小**

- **sv32**: 支持 4 GB（2^32）虚拟地址空间。
- **sv39**: 支持 512 GB（2^39）虚拟地址空间。
- **sv48**: 支持 256 TB（2^48）虚拟地址空间。

3. **每个页表项大小**

在这三种模式下，每个页表项的大小都是 **64 位**。这种一致性使得页表项结构和标志位保持统一。

4. **页大小**

这三种模式默认页大小均为 **4 KB**。不过，RISC-V 架构也允许使用更大的超页（例如 2 MB 或 1 GB），由页表项的级别决定。

### 对相似性的解释

这两段代码分别获取一级页目录表和二级页表的页表项，确保在页表中找到对应的物理页表地址。第一段代码 `pdep1` 负责找到一级页目录项，如果该项不存在且 `create` 为 `true`，则分配一个新的物理页，建立表项。第二段代码 `pdep0` 负责进入到二级页表项，再次进行存在性检查，不存在时同样创建。

sv32、sv39、sv48 三种模式在结构上都要求分级逐步访问页表，确保每一级都分配和初始化。这种分级逻辑的页表层级间的主要差异仅在于页表基地址的变化和每一级索引的位宽不同，而查找操作的基本流程保持不变：通过逐级索引定位到目标页表项并确保其存在。因此，每个层级的处理逻辑非常相似，这使得每个层级的代码结构几乎一致，只需调整基地址和索引偏移量即可适应不同层级的查找和分配需求。

```c
pde_t *pdep1 = &pgdir[PDX1(la)];
if (!(*pdep1 & PTE_V)) {
    struct Page *page;
    if (!create || (page = alloc_page()) == NULL) {
        return NULL;
    }
    set_page_ref(page, 1);
    uintptr_t pa = page2pa(page);
    memset(KADDR(pa), 0, PGSIZE);
    *pdep1 = pte_create(page2ppn(page), PTE_U | PTE_V);
}

pde_t *pdep0 = &((pde_t *)KADDR(PDE_ADDR(*pdep1)))[PDX0(la)];

if (!(*pdep0 & PTE_V)) {
    struct Page *page;
    if (!create || (page = alloc_page()) == NULL) {
        return NULL;
    }
    set_page_ref(page, 1);
    uintptr_t pa = page2pa(page);
    memset(KADDR(pa), 0, PGSIZE);
    *pdep0 = pte_create(page2ppn(page), PTE_U | PTE_V);
}
```

### 是否需要拆分 `get_pte()` 函数中的查找和分配功能？

我们组认为这种写法是很好的，并不需要拆分。将查找和分配合并在同一个函数中具有以下几个优点：

1. **减少代码重复**：通常在获取页表项时才会遇到缺失的情况，特别是在页表非法或未分配的情况下，才需要进行页表的创建。通过在一个函数内同时处理查找和分配，可以避免多次调用不同的函数来处理相同的情景。
   
2. **降低函数调用开销**：合并查找和分配到一个单一函数中，可以减少不必要的函数调用次数，从而降低整体开销。

3. **简化逻辑结构**：由于主要关注的是最终一级页表所给出的页，因此这种合并不仅简化了代码，还提高了性能。整个流程更加直观易懂，减少了潜在的错误点。

综上所述，我们认为保持现有的实现方式是合理的选择。

## 练习 3：给未被映射的地址映射上物理页（需要编程）

> 补充完成 do_pgfault（mm/vmm.c）函数，给未被映射的地址映射上物理页。设置访问权限的时候需要参考页面所在 VMA 的权限，同时需要注意映射物理页时需要操作内存控制结构所指定的页表，而不是内核的页表。
>
> 请在实验报告中简要说明你的设计实现过程。请回答如下问题：
>
> - 请描述页目录项（Page Directory Entry）和页表项（Page Table Entry）中组成部分对 ucore 实现页替换算法的潜在用处。
> - 如果 ucore 的缺页服务例程在执行过程中访问内存，出现了页访问异常，请问硬件要做哪些事情？
>   - 数据结构 Page 的全局变量（其实是一个数组）的每一项与页表中的页目录项和页表项有无对应关系？如果有，其对应关系是啥？

### 实现代码

页面错误处理函数 do_pgfault 的作用是在发生页面错误时，根据错误地址和错误码进行相应的处理。

**设计实现过程**：

1. `swap_in(mm, addr, &page)`：首先，该函数负责根据页表基地址和虚拟地址从磁盘读取数据，并将其写入内存，最终返回内存中的物理页。

2. `page_insert(mm->pgdir, page, addr, perm)`：随后，此函数将虚拟地址与内存中的物理页进行映射，确保它们之间的关联。

3. `swap_map_swappable(mm, addr, page, 0)`：最后，通过此函数将页面标记为可交换，以便在需要时可以将其从内存交换到磁盘中。

```c
int do_pgfault(struct mm_struct *mm, uint_t error_code, uintptr_t addr) {
    int ret = -E_INVAL;
    struct vma_struct *vma = find_vma(mm, addr);//查找包含错误地址的虚拟内存区域 (VMA)
    pgfault_num++;
    if (vma == NULL || vma->vm_start > addr) {//检查地址是否有效
        cprintf("not valid addr %x, and  can not find it in vma\n", addr);
        goto failed;
    }
    uint32_t perm = PTE_U;
    if (vma->vm_flags & VM_WRITE) {//设置页面权限
        perm |= (PTE_R | PTE_W);
    }
    addr = ROUNDDOWN(addr, PGSIZE);
    ret = -E_NO_MEM;
    pte_t *ptep=NULL;
    ptep = get_pte(mm->pgdir, addr, 1);//获取页表项 (PTE)
    /*处理页面错误*/
    if (*ptep == 0) {//页面不存在的情况：分配一个新的页面并建立映射
        if (pgdir_alloc_page(mm->pgdir, addr, perm) == NULL) {
            cprintf("pgdir_alloc_page in do_pgfault failed\n");
            goto failed;
        }
    } else {//页面存在但需要从交换区加载
        if (swap_init_ok) {
            struct Page *page = NULL;
            /*我们需要实现的部分*/
            swap_in(mm,addr,&page); //把从磁盘中得到的页放进内存中
            page_insert(mm->pgdir,page,addr,perm);//在页表中新增加一个映射，并且设置权限
            swap_map_swappable(mm,addr,page,1);//将该内存页设置为可交换，最后一个参数目前还没有用
            page->pra_vaddr = addr;//将虚拟地址addr存储到页面结构
        } else {
            cprintf("no swap_init_ok but ptep is %x, failed\n", *ptep);
            goto failed;
        }
   }
   ret = 0;
failed:
    return ret;
}
```

### 潜在用处

#### 页目录表项

在页替换算法中，页目录表项中的有效标志可以用来快速判断该页表是否有效。如果一个页表项无效，可能表明该页对应的内存已被换出或未分配，此时操作系统可以选择将其载入内存，或将其重新映射到物理内存。并且它通常还包括访问权限（如可读、可写、可执行等标志），这些信息对页替换算法同样有帮助。比如，如果一个页被标记为只读且正在被频繁修改，那么可能触发一个页替换策略，强制将其换出。

#### 页表项

页表项包含每个虚拟页面到物理页面的映射。它的有效标志指示该页面是否在内存中。如果该标志为无效，则意味着该页可能已被换出或尚未分配。在页替换算法中，系统会检查这个标志来决定是否需要换入页面或者更新其在内存中的位置。页表项还可能包括访问位（如访问过的位）。该位记录了页是否被访问过，这对于实现 `lru` 或 `clock` 等页替换算法非常有用。通过分析访问位，操作系统可以知道哪些页被频繁访问，哪些页被很少使用，从而决定是否将某个页面换出。它还包含页面的访问权限（如只读、可写等）。这些信息在页替换时也有用，因为它们可以帮助操作系统在替换页面时决定哪些页面可以安全地被修改，哪些页面需要保持不变，避免破坏进程的执行状态。

### 页访问异常硬件要做哪些事情

当 ucore 操作系统的缺页服务例程在执行过程中访问内存时，如果出现页访问异常，硬件需要执行以下步骤：

1. **检测异常**：当 CPU 尝试访问一个虚拟地址，而该地址在内存管理单元（MMU）中没有对应的物理地址映射，或者访问权限不一致时，会触发页访问异常。

2. **保存状态**：在 `kern_init` 函数中初始化物理内存后，调用 `idt_init` 来初始化中断描述表。设置寄存器 `sscratch` 为 0，表示当前执行的是内核代码；设置异常向量地址 `stvec` 指向 `__alltraps`，并设置 `sstatus` 以允许内核访问用户内存。

3. **异常处理流程**：当发生页访问异常时，`trapframe` 结构体中的相关寄存器会被修改。异常指令的地址会被存储在 `sepc` 寄存器中，访问的地址则存储在 `stval` 寄存器中。

4. **跳转至异常处理程序**：根据 `stvec` 设置的地址，跳转到中断处理程序，即 `trap.c` 文件中的 `trap` 函数。

5. **异常分派**：在 `trap` 函数中，会根据 `trapframe*tf->cause`（即 `scause`）的值，如果是 `CAUSE_LOAD_PAGE_FAULT` 或 `CAUSE_STORE_PAGE_FAULT`，则调用 `pgfault_handler` 进行进一步处理。

6. **缺页异常处理**：流程为 `trap` --> `trap_dispatch` --> `pgfault_handler` --> `do_pgfault`。首先保存当前异常原因，然后跳转到 `exception_handler` 中的 `CAUSE_LOAD_ACCESS` 处理缺页异常，接着跳转到 `pgfault_handler`，再到 `do_pgfault` 具体处理缺页异常。

7. **恢复执行或报错**：如果缺页异常处理成功，程序会返回到发生异常的地方继续执行。如果处理失败，则输出 `unhandled page fault` 错误信息。

这个过程确保了操作系统能够在遇到页访问异常时，能够正确地保存状态、分派异常处理，并在处理完毕后恢复执行或者报告错误。

### 对应关系

存在明确的对应关系。当一个页表项映射到物理地址时，该地址即对应于 `Page` 结构体数组中的一项。每个 `Page` 结构体代表一个物理页面，并且可以通过页表项间接地与之关联。页表项负责存储物理地址信息，这些信息可用于索引到相应的 `Page` 结构体，从而使得操作系统能够管理和跟踪物理内存的使用情况。

```c
struct Page {
    int ref;                        // page frame's reference counter
    uint_t flags;        // array of flags that describe the status of the page frame
    uint_t visited;
    unsigned int property;    // the num of free block, used in first fit pm manager
    list_entry_t page_link;         // free list link
    list_entry_t pra_page_link;     // used for pra (page replace algorithm)
    uintptr_t pra_vaddr;            // used for pra (page replace algorithm)
};
```

其中我们使用了一个 `visited` 变量，用来记录页面是否被访问。在 `map_swappable` 函数中，我们把换入的页面加入到 FIFO 的交换页队列中，此时页面已经被访问，因此将 `visited` 置为 1。

在 `clock_swap_out_victim` 函数中，我们根据算法筛选出可用来交换的页面。在 CLOCK 算法中，我们使用了 `visited` 成员：我们从队尾依次遍历到队头，查看 `visited` 变量，如果是 0，则表示该页面未被修改且未被访问过，可以将其作为候选页面进行替换。然后，我们将该页面从 FIFO 页面链表中删除。

由于 PTE_A 标志位表示内存页是否被访问过，因此 `visited` 与其相对应。

## 练习 4：补充完成 Clock 页替换算法（需要编程）

> 通过之前的练习，相信大家对 FIFO 的页面替换算法有了更深入的了解，现在请在我们给出的框架上，填写代码，实现 Clock 页替换算法（mm/swap_clock.c）。(提示: 要输出 curr_ptr 的值才能通过 make grade)
>
> 请在实验报告中简要说明你的设计实现过程。请回答如下问题：
>
> - 比较 Clock 页替换算法和 FIFO 算法的不同。

### 实现思路

**`_clock_init_mm`**：在该函数中我们首先要初始化 `pra_list_head` 为空链表，之后初始化当前指针 `curr_ptr` 指向 `pra_list_head`，表示当前页面替换位置为链表头并且将 mm 的私有成员指针指向 `pra_list_head`，用于后续的页面替换算法操作。

```c
static int
_clock_init_mm(struct mm_struct *mm)
{
    // cprintf(" mm->sm_priv %x in fifo_init_mm\n",mm->sm_priv);
    /*我们需要实现的部分*/
    list_init(&pra_list_head);    // 初始化pra_list_head为空链表
    curr_ptr = &pra_list_head;    // 初始化当前指针curr_ptr指向pra_list_head，表示当前页面替换位置为链表头
    mm->sm_priv = &pra_list_head; // 将mm的私有成员指针指向pra_list_head，用于后续的页面替换算法操作
    return 0;
}
```

**`_clock_map_swappable`**：在该函数中我们要实现把一个内存页放进交换区里面。因为题目中要求需要放进链表的最后面，并且数据结构是双向链表，所以我们只需放在 head 前面即可。最后我们需要把刚放进的内存页的访问位置 1。

```c
static int
_clock_map_swappable(struct mm_struct *mm, uintptr_t addr, struct Page *page, int swap_in)
{
    list_entry_t *entry = &(page->pra_page_link); // 获得要放进的内存页
    assert(entry != NULL && curr_ptr != NULL);
    /*我们需要实现的部分*/
    list_entry_t *head = (list_entry_t *)mm->sm_priv; // 获得链表头部
    assert(entry != NULL && head != NULL);
    list_add_before(head, entry); // 将页面page插入到页面链表pra_list_head的末尾
    page->visited = 1;            // 将页面的visited标志置为1，表示该页面已被访问
    return 0;
}
```

**`_clock_swap_out_victim`**：在该函数中我们需要实现 clock 算法的核心代码，也就是换出策略。因为 clock 算法是遍历环链表，所以刚好可以匹配我们的双向链表结构。所以我们在一个永真的 while 循环里面使用 curr_ptr 遍历这个双向链表结构，直到遇见一个访问位为 0 的内存页，就把它从链表中删除换出。其中我们需要注意两件事情：一个是当我们遇到访问位为 1 的内存页，我们需要把它的访问位置零。另一个则是，因为是一个双向链表，所以它可能会访问到 head，但因为 head 里面没有存储什么信息，所以我们这时候需要多做一个 list_next 步骤。

```c
static int
_clock_swap_out_victim(struct mm_struct *mm, struct Page **ptr_page, int in_tick)
{
    list_entry_t *head = (list_entry_t *)mm->sm_priv;
    assert(head != NULL);
    assert(in_tick == 0);
    /* Select the victim */
    //(1)  unlink the  earliest arrival page in front of pra_list_head qeueue
    //(2)  set the addr of addr of this page to ptr_page
    while (1)
    {
        /*我们需要实现的部分*/
        curr_ptr = list_next(curr_ptr); // 遍历页面链表pra_list_head，查找最早未被访问的页面
        if (curr_ptr == head)
        {
            curr_ptr = list_next(curr_ptr); // 如果访问到了head，多做一个list_next步骤
            if (curr_ptr == head)
            {
                *ptr_page = NULL;
                break;
            }
        }
        struct Page *page = le2page(curr_ptr, pra_page_link); // 获取当前页面对应的Page结构指针
        if (page->visited == 0)
        {
            *ptr_page = page; // 如果当前页面未被访问，则将该页面从页面链表中删除，并将该页面指针赋值给ptr_page作为换出页面
            list_del(curr_ptr);
            cprintf("curr_ptr %p\n", curr_ptr);
            ;
            break;
        }
        else
        {
            page->visited = 0; // 如果当前页面已被访问，则将visited标志置为0，表示该页面已被重新访问
        }
    }
    return 0;
}
```

### Clock 页替换算法和 FIFO 算法的不同

- **Clock 算法**：每次添加新页面时，该页面会被添加到链表的尾部。当需要换出页面时，系统会遍历整个链表以查找最近未被使用的页面进行替换。

- **FIFO（先进先出）算法**：在这种策略下，链表被视为一个队列。新加入的页面总是被放置在队列的末尾（即链表头部）。而当需要淘汰某个页面时，则直接移除位于队首位置（即链表尾部）的那个页面，无论它最近是否有过访问记录。

## 练习 5：阅读代码和实现手册，理解页表映射方式相关知识（思考题）

> 如果我们采用”一个大页“ 的页表映射方式，相比分级页表，有什么好处、优势，有什么坏处、风险？

### 好处与优势

1. **减少页表级数**： 在分级页表中，内存空间需要多级页表来映射虚拟地址，而使用一个大页时，可以一次性映射较大的内存区域，这样可以减少页表的层级数。内存的映射速度更快，页表查找的延迟会降低。
2. **减少页表项的数量**： 分级页表中每一层都需要存储页表项，而大页映射方式可以显著减少需要维护的页表项的数量。例如，使用 `2MB` 或 `1GB` 的大页时，整个虚拟内存空间可能只需要一个页表项，而分级页表需要多个页表项来表示同一块内存区域。
3. **提升 `TLB` 的命中率**： `TLB` 是用来缓存页表项的硬件组件，使用大页时，每个页表项映射的内存空间更大，这样能提高 `TLB` 的命中率，从而提高访问速度。因为大页可以将更多的虚拟地址映射到物理内存，从而减少 `TLB` 缓存失效的次数。
4. **降低页表管理的开销**： 由于页表项数量大幅减少，操作系统需要进行的页表管理操作（例如，分页处理、更新页表项等）也会相应减少。这意味着在进行进程切换或内存映射时，操作系统的开销会降低。
5. **适合大内存、大数据块的应用**： 对于需要大量内存的应用（例如数据库、大型科学计算等），大页映射方式非常适合。大页能够提供较大的内存块连续映射，减少了因频繁分页导致的性能下降。

### 坏处与风险

1. **内存碎片**： 使用大页会导致内存的碎片化风险增加，尤其是在内存使用不均匀或分配不连续时。大页虽然可以减少页表的数量，但由于它们必须以固定的较大块分配内存，如果进程的内存需求较为零散或不规则，可能导致一些大页无法有效利用，浪费内存。

2. **较低的内存利用率**： 大页是以固定大小分配的，如果实际需求的小于大页的大小，那么即使进程只需要少量内存，也会占用一个完整的大页，导致内存浪费。相比之下，分级页表可以按需分配更小的内存单元，从而提高内存利用率。

3. **操作系统支持要求高**： 对于大页的管理，操作系统需要支持更复杂的内存管理机制。例如，必须能够处理内存映射的大页、管理和交换大页等。如果操作系统对大页的支持不足，可能导致效率下降，甚至造成兼容性问题。

4. **TLB 缓存溢出**： 虽然大页有可能提高 TLB 的命中率，但如果程序的内存访问模式较为零散（访问多个不同的大页），那么 TLB 缓存可能会频繁溢出，导致频繁的页表查找和较高的访问延迟。

5. **系统开销和配置复杂性**： 在一些系统中，为了支持大页，可能需要更复杂的硬件支持（例如，特定的 TLB 结构或特定的内存访问模式）。此外，操作系统也可能需要更多的逻辑来处理大页的分配、回收和映射操作，这可能导致配置和管理的复杂性增加。

## 扩展练习 Challenge：实现不考虑实现开销和效率的 `LRU` 页替换算法（需要编程）

> challenge 部分不是必做部分，不过在正确最后会酌情加分。需写出有详细的设计、分析和测试的实验报告。完成出色的可获得适当加分。

### 实现思路

维护一个活动页链表，当我们访问这个链表内已经有的内存页的虚拟地址时，把对应的内存页从链表中删除，并插入到链表头。而当访问没有在这个链表的内存页时直接插入到链表头即可。这样每次新访问的内存页会一直在链表头，而最久没有访问的页会在链表尾部，我们替换的时候直接替换到尾部即可。

### 具体代码

前几部分的代码跟 FIFO 算法基本一样，不需要太大改变。

```c
static list_entry_t pra_list_head;//链表头

static int
_lru_init_mm(struct mm_struct *mm)
{     
    list_init(&pra_list_head);
    mm->sm_priv = &pra_list_head;
    return 0;
}

static int
_lru_map_swappable(struct mm_struct *mm, uintptr_t addr, struct Page *page, int swap_in)
{
    list_entry_t *head=(list_entry_t*) mm->sm_priv;
    list_entry_t *entry=&(page->pra_page_link);
 
    assert(entry != NULL && head != NULL);

    // 将最近访问的页面添加到链表头部
    list_add(head, entry);
    return 0;
}

static int
_lru_swap_out_victim(struct mm_struct *mm, struct Page ** ptr_page, int in_tick)
{
    list_entry_t *head=(list_entry_t*) mm->sm_priv;
    assert(head != NULL);
    assert(in_tick==0);
    
    // 选择最久未使用的页面进行替换
    list_entry_t* entry = list_prev(head);
    if (entry != head) {
        list_del(entry);
        *ptr_page = le2page(entry, pra_page_link);
    } else {
        *ptr_page = NULL;
    }
    return 0;
}
```

为了检查访问的内存页是否已经在链表中，新增加了一个函数 update_or_ignore，专门来检查访问的虚拟地址是否已经被映射。

```c
static void
update_or_ignore(uintptr_t addr) {
    list_entry_t *head = &pra_list_head, *le = head;
    
    while ((le = list_prev(le)) != head) {
        struct Page *curr = le2page(le, pra_page_link);
        if (curr->pra_vaddr == addr) {
            list_del(le);         // 删除找到的页
            list_add(head, le);   // 将页移到链表头部
            return;               // 直接返回，不必继续查找
        }
    }
}
```

也稍微修改了一下检查函数，输出每次访问内存过后的链表内部情况。

```c
static void
printlist() {
    cprintf("--------head----------\n");
    list_entry_t *head = &pra_list_head, *le = head;
    while ((le = list_next(le)) != head)
    {
        struct Page* page = le2page(le, pra_page_link);
        cprintf("vaddr: %x\n", page->pra_vaddr);
    }
    cprintf("--------tail----------\n");
}

static void
write_and_check(uintptr_t addr, unsigned char value) {
    cprintf("write Virt Page 0x%x in lru_check_swap\n", addr);
    update_or_ignore(addr);
    *(unsigned char *)addr = value;
}

static int
_lru_check_swap(void) {
    write_and_check(0x3000, 0x0c);
    printlist();
    write_and_check(0x1000, 0x0a);
    printlist();
    write_and_check(0x4000, 0x0d);
    printlist();
    write_and_check(0x2000, 0x0b);
    printlist();
    write_and_check(0x5000, 0x0e);
    printlist();
    write_and_check(0x2000, 0x0b);
    printlist();
    write_and_check(0x1000, 0x0a);
    printlist();
    write_and_check(0x2000, 0x0b);
    printlist();
    write_and_check(0x3000, 0x0c);
    printlist();
    write_and_check(0x4000, 0x0d);
    printlist();
    write_and_check(0x5000, 0x0e);
    printlist();
    write_and_check(0x1000, 0x0a);
    printlist();

    return 0;
}
```

### 实验结果

```shell
write Virt Page 0x3000 in lru_check_swap
--------head----------
vaddr: 3000
vaddr: 4000
vaddr: 2000
vaddr: 1000
--------tail----------
write Virt Page 0x1000 in lru_check_swap
--------head----------
vaddr: 1000
vaddr: 3000
vaddr: 4000
vaddr: 2000
--------tail----------
write Virt Page 0x4000 in lru_check_swap
--------head----------
vaddr: 4000
vaddr: 1000
vaddr: 3000
vaddr: 2000
--------tail----------
write Virt Page 0x2000 in lru_check_swap
--------head----------
vaddr: 2000
vaddr: 4000
vaddr: 1000
vaddr: 3000
--------tail----------
write Virt Page 0x5000 in lru_check_swap
Store/AMO page fault
page fault at 0x00005000: K/W
swap_out: i 0, store page in vaddr 0x3000 to disk swap entry 4
--------head----------
vaddr: 5000
vaddr: 2000
vaddr: 4000
vaddr: 1000
--------tail----------
write Virt Page 0x2000 in lru_check_swap
--------head----------
vaddr: 2000
vaddr: 5000
vaddr: 4000
vaddr: 1000
--------tail----------
write Virt Page 0x1000 in lru_check_swap
--------head----------
vaddr: 1000
vaddr: 2000
vaddr: 5000
vaddr: 4000
--------tail----------
write Virt Page 0x2000 in lru_check_swap
--------head----------
vaddr: 2000
vaddr: 1000
vaddr: 5000
vaddr: 4000
--------tail----------
write Virt Page 0x3000 in lru_check_swap
Store/AMO page fault
page fault at 0x00003000: K/W
swap_out: i 0, store page in vaddr 0x4000 to disk swap entry 5
swap_in: load disk swap entry 4 with swap_page in vadr 0x3000
--------head----------
vaddr: 3000
vaddr: 2000
vaddr: 1000
vaddr: 5000
--------tail----------
write Virt Page 0x4000 in lru_check_swap
Store/AMO page fault
page fault at 0x00004000: K/W
swap_out: i 0, store page in vaddr 0x5000 to disk swap entry 6
swap_in: load disk swap entry 5 with swap_page in vadr 0x4000
--------head----------
vaddr: 4000
vaddr: 3000
vaddr: 2000
vaddr: 1000
--------tail----------
write Virt Page 0x5000 in lru_check_swap
Store/AMO page fault
page fault at 0x00005000: K/W
swap_out: i 0, store page in vaddr 0x1000 to disk swap entry 2
swap_in: load disk swap entry 6 with swap_page in vadr 0x5000
--------head----------
vaddr: 5000
vaddr: 4000
vaddr: 3000
vaddr: 2000
--------tail----------
write Virt Page 0x1000 in lru_check_swap
Store/AMO page fault
page fault at 0x00001000: K/W
swap_out: i 0, store page in vaddr 0x2000 to disk swap entry 3
swap_in: load disk swap entry 2 with swap_page in vadr 0x1000
--------head----------
vaddr: 1000
vaddr: 5000
vaddr: 4000
vaddr: 3000
--------tail----------
count is 1, total is 8
check_swap() succeeded!
```

可以看到，每次访问页面时，如果发生缺页异常，该页面将被添加到链表的头部。而当需要移除页面时，则从链表的尾部进行删除。
