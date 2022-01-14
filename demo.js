class Optional {
  constructor(value) {
    this.value = value;
  }

  isPresent() {
    return this.value != null;
  }

  map(func) {
    if (Array.isArray(this.value)) {
      let ret = [];
      this.value.forEach((i) => {
        let result = func.call(null, i);
        Array.isArray(result)
          ? ret.splice(ret.length, 0, ...result)
          : ret.push(result);
      });
      return new Optional(ret);
    }
    return new Optional(
      !this.isPresent() ? null : func.apply(this, this.value)
    );
  }

  toString() {
    return Array.isArray(this.value) ? this.value.join("|") : this.value;
  }
}

let school = [
  {
    name: "一年级",
    class: [
      {
        name: "一班",
        students: [
          { name: "一年级一班zs", age: 18 },
          { name: "一年级一班ls", age: 19 },
        ],
      },
      {
        name: "二班",
        students: [
          { name: "一年级二班zs", age: 18 },
          { name: "一年级二班ls", age: 19 },
        ],
      },
    ],
  },
  {
    name: "二年级",
    class: [
      {
        name: "一班",
        students: [
          { name: "二年级一班zs", age: 18 },
          { name: "二年级一班ls", age: 19 },
        ],
      },
      {
        name: "二班",
        students: [
          { name: "二年级二班zs", age: 18 },
          { name: "二年级二班ls", age: 19 },
        ],
      },
    ],
  },
];
console.log(
  new Optional(school)
    .map((i) => i.class)
    .map((i) => i.students)
    .map((i) => i.name)
    .toString()
);
