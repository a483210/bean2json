# Bean 2 Json

- [java2jason](https://github.com/linsage/java2json)的修改版本
- 优化了`Java`转`Json`的功能、添加对`Kotlin`的支持

## 使用



### 功能说明

- 1.ToLess
    - 将Pojo转换为Json，并且使用生成数据填充值

```json
{
  "name": "a483210"
}
```

- 2.ToComment
    - 将Pojo转换为Json，并且使用注释填充值

```json
{
  "name": "用户名称"
}
```

- 3.ToReadable
    - 将Pojo转换为Json，使用生成数据填充值，并且将注释添加到Json中

```
{
  "name": "a483210" // 用户名称
}
```

### 数据生成模式

- 1.`DefaultValue`
    - 优先取变量的默认值，为空则使用对应类型的默认值
- 2.`TypeName`
    - 使用变量的类型名作为值
- 3.`MockData`
    - 会创建假数据，默认会通过变量名称匹配对应的数据类型，如果匹配不到则随机产生数据